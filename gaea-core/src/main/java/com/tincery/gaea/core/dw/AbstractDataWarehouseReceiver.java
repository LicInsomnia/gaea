package com.tincery.gaea.core.dw;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static com.tincery.gaea.core.base.tool.util.DateUtils.DAY;
import static com.tincery.gaea.core.base.tool.util.DateUtils.MINUTE;

/**
 * @author gxz gongxuanzhang@foxmail.com 此模块内容形式： 默认执行 将一行CSV传递给子类 子类需要对CSV进行处理
 **/
@Slf4j
public abstract class AbstractDataWarehouseReceiver implements Receiver {
    protected static ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            CPU + 1,
            CPU * 2,
            10,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(200),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    protected static CountDownLatch countDownLatch;

    protected static LongAdder longAdder = new LongAdder();

    private List<CsvFilter> csvFilterList;

    @Override
    public void receive(TextMessage textMessage) {
        try {
            LocalDateTime now = LocalDateTime.now();
            log.info("消息传递时间：{}；执行时间：{}", DateUtils.format(textMessage.getJMSTimestamp()), now.format(DateUtils.DEFAULT_DATE_PATTERN));
            // 加载当前程序模块对应的run_config表配置
            JSONObject runConfig = DataWarehouseRunController.getRunConfig(ApplicationInfo.getCategory());
            // 获取run_config中的startTime（读CSV的开始时间）
            LocalDateTime startTime = DateUtils.Date2LocalDateTime(runConfig.getDate("startTime"));
            LocalDateTime dwTime = now.plusHours(-1);
            if (dwTime.compareTo(startTime) <= 0) {
                log.info("执行时间临近当前时间1小时，本次执行跳出");
                return;
            }
            int recollTime = runConfig.getInteger("recolltime");
            LocalDateTime endTime = startTime.plusMinutes(recollTime);
            if (dwTime.compareTo(endTime) < 0) {
                endTime = dwTime;
            }
            dataWarehouseAnalysis(startTime, endTime);
            runConfig.replace("starttime", endTime);
            log.info("更新[{}]运行配置：{}", ApplicationInfo.getCategory(), runConfig);
            DataWarehouseRunController.reWriteRunconfig(ApplicationInfo.getCategory(), runConfig);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /****
     * 每个执行器都有自己的csv表头
     * @author gxz
     * @return java.lang.String
     **/
    public String[] getHead(String category) {
        String headString;
        switch (category) {
            case "session":
                headString = HeadConst.SESSION_HEADER;
                break;
            case "ssl":
                headString = HeadConst.SSL_HEADER;
                break;
            case "openvpn":
                headString = HeadConst.OPENVPN_HEADER;
                break;
            case "dns":
                headString = HeadConst.DNS_HEADER;
                break;
            case "http":
                headString = HeadConst.HTTP_HEADER;
                break;
            case "email":
                headString = HeadConst.EMAIL_HEADER;
                break;
            case "isakmp":
                headString = HeadConst.ISAKMP_HEADER;
                break;
            case "ssh":
                headString = HeadConst.SSH_HEADER;
                break;
            case "ftp_telnet":
                headString = HeadConst.FTPANDTELNET_HEADER;
                break;
            case "esp_ah":
                headString = HeadConst.ESPANDAH_HEADER;
                break;
            default:
                return null;
        }
        return headString.split(HeadConst.CSV_SEPARATOR_STR);
    }

    private void analysisList(List<Pair<String, String>> csvPaths) {
        for (Pair<String, String> csvPath : csvPaths) {
            CsvReader csvReader;
            try {
                csvReader = CsvReader.builder().file(csvPath.getValue()).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }
            analysis(csvPath.getKey(), csvReader);
        }

    }

    public void dataWarehouseAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("本次处理开始时间：{}，结束时间：{}", startTime, endTime);
        List<Pair<String, String>> csvPaths = getCsvDataSet(startTime, endTime);
        long st = Instant.now().toEpochMilli();
        log.info("开始解析CSV数据...");
        System.out.println("一共有" + csvPaths.size() + "个文件");

        List<List<Pair<String, String>>> partition = Lists.partition(csvPaths, csvPaths.size() / 4 + 1);
        countDownLatch = new CountDownLatch(csvPaths.size());
        for (List<Pair<String, String>> pairs : partition) {
            executorService.execute(() -> analysisList(pairs));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        free();
        log.info("共用时{}毫秒", (Instant.now().toEpochMilli() - st));
    }


    protected List<String> getCsvDataSetBySessionCategory(String sesionCategory, LocalDateTime startTime, LocalDateTime endTime) {
        long endTimeLong = DateUtils.LocalDateTime2Long(endTime);
        long startTimeLong = DateUtils.LocalDateTime2Long(startTime);
        String rootPath = NodeInfo.getDataWarehouseCsvPathByCategory(sesionCategory);
        List<String> list = new ArrayList<>();
        long timeStamp = startTimeLong = startTimeLong / MINUTE * MINUTE;
        endTimeLong = endTimeLong / MINUTE * MINUTE + MINUTE;
        while (timeStamp <= endTimeLong) {
            File path = new File(rootPath + "/" + ToolUtils.stamp2Date(timeStamp, "yyyyMMdd"));
            if (path.exists() && path.isDirectory()) {
                String[] files = path.list();
                if (null != files) {
                    for (String fileName : files) {
                        if (!fileName.startsWith(sesionCategory)) {
                            continue;
                        }
                        String[] elements = fileName.split("\\.")[0].split("_");
                        String timeStampStr = elements[elements.length - 1];
                        long ts = ToolUtils.date2Stamp(timeStampStr, "yyyyMMddHHmm");
                        if (startTimeLong <= ts && endTimeLong > ts) {
                            list.add(path + "/" + fileName);
                        }
                    }
                }
            }
            timeStamp += DAY;
        }
        return list;
    }

    public void free() {
        throw new UnsupportedOperationException();
    }

    public void analysis(String sessionCategory, CsvReader csvReader) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取csv文件名的集合 集合中一组pair中key：sessionCategory;value：文件名 同一个sessionCategory可能对应多组文件 例: [ {key1:value1} {key1:value2}
     * {key2:value3} ]
     */
    public abstract List<Pair<String, String>> getCsvDataSet(LocalDateTime startTime, LocalDateTime endTime);

    protected List<CsvFilter> registryFilter(CsvFilter... csvFilterList) {
        if (null == this.csvFilterList) {
            this.csvFilterList = new ArrayList<>();
        }
        this.csvFilterList.addAll(Arrays.asList(csvFilterList));
        return this.csvFilterList;
    }

}
