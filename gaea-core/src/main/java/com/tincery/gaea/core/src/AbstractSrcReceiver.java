package com.tincery.gaea.core.src;


import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author gxz gongxuanzhang@foxmail.com 汇聚执行器 此方法的执行策略是 将准备写入的csv内容存到map中 最终统一执行写入  流程如下图 Src处理器  此类监听由Producer 发送的Mq消息
 * 处理内容
 **/
@Getter
@Setter
@Slf4j
public abstract class AbstractSrcReceiver<M extends AbstractSrcData> implements Receiver {

    protected static ThreadPoolExecutor executorService;

    static {
        executorService = new ThreadPoolExecutor(
                CPU + 1,
                CPU * 2,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(4096),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    protected final Map<String, FileWriter> csvDataHandle = new HashMap<>();
    protected SrcProperties properties;
    /**
     * 当前csv输出中缓存的csv记录数
     */
    protected int csvCount;
    /***此map存放 用fileName 信息做key  行内容作为value的map*/
    protected Map<String, List<String>> csvMap = new HashMap<>();
    /**
     * 当前csv输出中缓存的csv记录中的最小时间
     */
    protected long minTime;
    /**
     * 当前csv输出中缓存的csv记录中的最大时间
     */
    protected long maxTime;
    protected SrcLineAnalysis<M> analysis;


    public abstract void setProperties(SrcProperties properties);

    /****
     * 每个执行器都有自己的csv表头
     * @author gxz
     * @return java.lang.String
     **/
    public abstract String getHead();

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        log.info("消息传递时间：{}；执行时间：{}", DateUtils.format(textMessage.getJMSTimestamp()), DateUtils.now());
        String text = textMessage.getText();
        File file = new File(text);
        log.info("开始解析文件:[{}]", file.getName());
        long startTime = System.currentTimeMillis();
        analysisFile(file);
        long l = Instant.now().toEpochMilli();
        this.clearFile(file);
        System.out.println("clearFile用了"+(l-Instant.now().toEpochMilli()));
        this.free();
        System.out.println("free用了"+(l-Instant.now().toEpochMilli()));
        log.info("文件:[{}]处理完成，用时{}毫秒", file.getName(), (System.currentTimeMillis() - startTime));
    }

    /**
     * 解析src层文件获取逐条会话记录
     *
     * @param file src层文件
     */
    protected List<String> getLines(File file) {
        return FileUtils.readLine(file);
    }

    /**
     * 多线程实现执行
     * 如需要多线程实现 请重写此方法
     *
     * @author gxz
     **/
    protected void analysisFile(File file) {
        if (!file.exists()) {
            log.info("文件：{}已被处理" + file.getAbsolutePath());
            return;
        }
        List<String> lines = getLines(file);
        if (lines.isEmpty()) {
            return;
        }
        int executor = this.properties.getExecutor();
        if (executor <= 1 || executor <= lines.size()) {
            analysisLine(lines);
        } else {
            List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);
            CountDownLatch countDownLatch = new CountDownLatch(partitions.size());
            for (List<String> partition : partitions) {
                executorService.execute(() -> {
                    try {
                        analysisLine(partition);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        log.error("解析实体时出现特殊异常");
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /****
     * 解析一行记录 填充到相应的容器中
     * @author gxz
     * @param lines 多条记录
     **/
    protected void analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            M pack;
            try {
                pack = this.analysis.pack(line);
                pack.adjust();
            } catch (Exception e) {
                log.error("错误信息:{},错误SRC：{}", e.getMessage(),line);
                continue;
            }
            this.putCsvMap(pack);
        }
    }

    /**
     * 文件输出结束之后 最后释放
     **/
    protected void free() {
        outputCsvData();
        outputAlarm();
    }

    /***
     * 模板方法 输出告警 如果对应的模块不需要告警  不需要重写此方法
     *  如果需要告警 请在相应模块中重写此方法
     **/
    protected void outputAlarm() {
        AlarmRule.writeAlarm(NodeInfo.getAlarmMaterial(), "", this.properties.getMaxLine());
        AlarmRule.writeEvent(NodeInfo.getEventData(), "", this.properties.getMaxLine());
    }


    /**
     * @param data 单一Src层记录
     * @author gxz 处理单条src层记录
     */
    protected void putCsvMap(M data) {
        if (RuleRegistry.getInstance().matchLoop(data)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        String fileName = data.getDateSetFileName(category);
        this.appendCsvData(
                fileName,
                data.toCsv(HeadConst.CSV_SEPARATOR),
                data.getCapTime()
        );
    }


    /**
     * 清理原文件
     */
    protected void clearFile(File file) {
        if (this.properties.isTest()) {
            return;
        }
        bakFile(file);
        log.info("删除文件{},{}",file.getName(),file.delete()?"成功":"失败");
    }

    /****
     * 源文件是否需要备份
     **/
    protected void bakFile(File file) {
        if (this.properties.isBack()) {
            String bakPathDir = NodeInfo.getBakByCategory(ApplicationInfo.getCategory());
            FileUtils.checkPath(bakPathDir);
            String srcPath = file.getAbsolutePath();
            bakPathDir += ("/" + file.getName());
            FileUtils.fileMove(srcPath, bakPathDir);
            log.info("备份文件[{}]至[{}]]", srcPath, bakPathDir);
        }
    }

    /**
     * 输出本地数据文件
     */
    protected synchronized void appendCsvData(String fileName, String csvLine, long capTime) {
        List<String> csvLines;
        if (this.csvMap.containsKey(fileName)) {
            csvLines = this.csvMap.get(fileName);
        } else {
            csvLines = new ArrayList<>();
            this.csvMap.put(fileName, csvLines);
        }
        csvLines.add(csvLine);
        if (++this.csvCount >= this.properties.getMaxLine()) {
            outputCsvData();
        }
        if (this.minTime == 0 || capTime < this.minTime) {
            this.minTime = capTime;
        }
        if (capTime > this.maxTime) {
            this.maxTime = capTime;
        }
    }

    public synchronized void outputCsvData() {
        if (this.csvMap.isEmpty()) {
            this.minTime = 0;
            this.maxTime = 0;
            return;
        }
        long st = System.currentTimeMillis();
        String dataPath = getDataWarehouseCsvPath();
        checkFiles(dataPath, this.minTime, this.maxTime);
        for (Map.Entry<String, List<String>> entry : this.csvMap.entrySet()) {
            String filePath = dataPath + entry.getKey();
            List<String> csvLines = entry.getValue();
            for (String csvLine : csvLines) {
                FileWriter fileWriter;
                if (this.csvDataHandle.containsKey(filePath)) {
                    fileWriter = this.csvDataHandle.get(filePath);
                    fileWriter.write(csvLine);
                } else {
                    fileWriter = new FileWriter();
                    File file = new File(filePath);
                    if (file.exists()) {
                        fileWriter.set(file.toString(), true);
                    } else {
                        fileWriter.setCSV(filePath, this.getHead(), true);
                    }
                    fileWriter.write(csvLine);
                    this.csvDataHandle.put(filePath, fileWriter);
                    if (this.csvDataHandle.size() > 120) {
                        close();
                    }
                }
            }
        }
        close();
        this.csvDataHandle.clear();
        this.csvMap.clear();
        log.info("输出了{}条记录，用时{}毫秒", this.csvCount, DateUtils.duration(st));
        this.csvCount = 0;
        this.minTime = 0;
        this.maxTime = 0;
    }

    protected String getDataWarehouseCsvPath() {
        return ApplicationInfo.getDataWarehouseCsvPathByCategory();
    }

    protected String getErrorPath() {
        return ApplicationInfo.getErrorByCategory();
    }

    /****
     * 如果句柄超过了阈值 就先关闭
     * @author Insomnia
     **/
    protected void close() {
        for (FileWriter fileWriter : this.csvDataHandle.values()) {
            fileWriter.close();
        }
        this.csvDataHandle.clear();
    }


    /**
     * @author Insomnia 整体检测数据集输出路径
     */
    protected void checkFiles(String subPath, long min, long max) {
        long minPath = Long.parseLong(DateUtils.format(min, "yyyyMMdd"));
        long maxPath = Long.parseLong(DateUtils.format(max, "yyyyMMdd"));
        while (minPath <= maxPath) {
            String path = subPath + "/" + minPath;
            FileUtils.checkPath(path);
            min += DateUtils.DAY;
            minPath = Long.parseLong(DateUtils.format(min, "yyyyMMdd"));
        }
    }

    public boolean isMultiThreadExecutor() {
        return this.properties.getExecutor() != 0;
    }

    public static void main(String[] args) {
        File file = new File("bbb.txt");
        List<String> list = FileUtils.readLine(file);
        FileUtils.checkPath("aaa.txt");
        System.out.println(list.size());
        FileUtils.fileMove("bbb.txt","aaa.txt");
        System.out.println(file.delete());
    }

}
