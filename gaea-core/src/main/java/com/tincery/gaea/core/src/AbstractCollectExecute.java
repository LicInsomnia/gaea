package com.tincery.gaea.core.src;


import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.CerChain;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 汇聚执行器
 * 此方法的执行策略是 将准备写入的csv内容存到map中 最终统一执行写入  流程如下图
 * +-------------+               +------------+
 * |             |               |            |
 * | 多 个 TXT    | ----------->  |            |
 * |             |               |  容   器    |
 * +-------------+               |            |
 * |            |
 * +-------------+               |            |
 * |             |               |            |------------->统一写入CSV
 * |  多 个 TXT   |               |            |
 * |             | ----------->  |            |
 * +-------------+               |            |
 * |            |
 * +-------------+               |            |
 * |             |               |            |
 * |   多 个 TXT  | ----------->  |            |
 * |             |               |            |
 * +-------------+               +------------+
 **/
@Getter
@Setter
@Slf4j
public abstract class AbstractCollectExecute<P extends AbstractSrcProperties, M extends AbstractSrcData> extends AbstractSrcExecute<P> {


    /***此map存放 用fileName 信息做key  行内容作为value的map*/
    protected Map<String, List<String>> csvMap = new HashMap<>();

    protected int csvCount;

    protected long minTime;

    protected long maxTime;

    protected SrcLineAnalysis<M> analysis;

    private static final Object[] EMPTY = new Object[]{};

    public static final String SRC_SEPARATOR = new String(new char[]{0x07});

    public static final char CSV_SEPARATOR = 0x07;

    protected final Map<String, FileWriter> csvDataHandle = new HashMap<>();

    protected int metaCount;


    protected CerChain cerChain;

    /****
     * 每个执行器都有自己的csv表头
     * @author gxz
     * @return java.lang.String
     **/
    public abstract String getHead();

    public void setCerChain(CerChain cerChain) {

    }

    @Override
    public void execute() {
        List<File> fileList = this.getTxtFiles();
        System.out.println("拿到了"+fileList.size()+"个文件");
        for (File file : fileList) {
            long startTime = Instant.now().toEpochMilli();
            List<String> lines = FileUtils.readLine(file);
            if (lines.isEmpty()) {
                continue;
            }
            log.info("{}共有{}行，开始解析", file.getName(), lines.size());
            long timeStamp;
            if (isMultiThreadExecutor()) {
                timeStamp = multiThreadExecute(lines);
            } else {
                timeStamp = analysisLine(lines);
            }
            log.info("解析{}，共花费{}毫秒", file.getName(), (timeStamp - startTime));
            this.clearFile(file);
        }
        // 本次处理结束关闭文件句柄
        this.free();
    }


    protected void outputMetaData(String line) {
        try (FileWriter fileWriter = new FileWriter(NodeInfo.getAlarmData() + NodeInfo.getCategory() + "_" + System.currentTimeMillis() + ".json")) {
            fileWriter.write(line);
            this.metaCount++;
            if (this.metaCount >= this.maxLine()) {
                fileWriter.set(NodeInfo.getTempData() + NodeInfo.getCategory() + "_" + System.currentTimeMillis() + ".json");
                this.metaCount = 0;
            }
        }
    }


    /*****
     * 多线程实现执行  基类默认实现为单线程  同analysisLine
     * 如需要多线程实现 请重写此方法
     * @author gxz
     * @return long
     **/
    public long multiThreadExecute(List<String> lines) {
        return analysisLine(lines);
    }

    /**
     * 文件输出结束之后 最后释放
     **/
    protected void free() {
        outputCsvData();
        outputAlarm();
        //输出证书链记录
        outputCerChain();
    }

    /***
     * 模板方法 输出告警 如果对应的模块不需要告警  不需要重写此方法
     *  如果需要告警 请在相应模块中重写此方法
     **/
    protected void outputAlarm() {
        AlarmRule.writeAlarm(NodeInfo.getAlarmData(), "", this.maxLine());
        AlarmRule.writeEvent(NodeInfo.getEventData(), "", this.maxLine());
    }

    protected void outputCerChain() {
        if (this.cerChain == null || this.cerChain.isEmpty()) {
            return;
        }
        StringBuilder path = new StringBuilder();
        path.append(NodeInfo.getCommonData()).append("cerchain/").append(this.cerChain.getSubPath());
        FileUtils.checkPath(path.toString());
        this.cerChain.output(path.toString());
        this.cerChain.clear();
    }


    /****
     * 解析一行记录 填充到相应的容器中
     * @author gxz
     * @param lines 多条记录
     * @return long
     **/
    protected long analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                M pack;
                try {
                    pack = this.analysis.pack(line);
                    pack.adjust();
                } catch (Exception e) {
                    log.error("解析实体出现了问题{}", line);
                    // TODO: 2020/9/8 实体解析有问题告警
                    e.printStackTrace();
                    continue;
                }
                this.putCsvMap(pack);
            }
        }
        return Instant.now().toEpochMilli();
    }


    /**
     * @param data 单一session信息
     * @author gxz
     * 处理单条session记录
     */
    protected void putCsvMap(M data) {
        if (RuleRegistry.getInstance().matchLoop(data)) {
            // 过滤过滤
            return;
        }
        String category = NodeInfo.getCategory();
        if (data.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = data.getDateSetFileName(category);
        this.appendCsvData(fileName,
                data.toCsv(CSV_SEPARATOR),
                data.capTime);
    }


    /**
     * 清理原文件
     */
    protected void clearFile(File file) {
        bakFile(file);
        if (!isTest()) {
            if (file.delete()) {
                log.info("删除文件{}", file.getName());
            }
        }
    }

    /****
     * 源文件是否需要备份
     **/
    protected void bakFile(File file) {
        if (isBak()) {
            String bakPathDir = NodeInfo.getBackData();
            FileUtils.checkPath(bakPathDir);
            String srcPath = file.getAbsolutePath();
            bakPathDir += file.getName();
            FileUtils.fileMove(srcPath, bakPathDir);
        }
    }

    /**
     * 输出本地数据文件
     */
    protected synchronized void appendCsvData(String fileName, String csvLine, long captime) {
        List<String> csvLines = this.csvMap.replace(fileName, new ArrayList<>());
        csvLines = csvLines == null ? this.csvMap.get(fileName) : csvLines;
        csvLines.add(csvLine);
        if (++this.csvCount >= maxLine()) {
            outputCsvData();
        }
        if (this.minTime == 0 || captime < this.minTime) {
            this.minTime = captime;
        }
        if (captime > this.maxTime) {
            this.maxTime = captime;
        }
    }

    @Override
    public synchronized void outputCsvData() {
        if (this.csvMap.isEmpty()) {
            this.minTime = 0;
            this.maxTime = 0;
            return;
        }
        long st = System.currentTimeMillis();
        String dataPath = NodeInfo.getCommonData() + NodeInfo.getCategory() + "/";
        this.checkFiles(dataPath, this.minTime, this.maxTime);
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
                    log.info("向{}输出了{}条记录，用时{}", filePath, this.csvCount, DateUtils.duration(st));
                    this.csvDataHandle.put(filePath, fileWriter);
                    if (this.csvDataHandle.size() > 120) {
                        close();
                    }
                }
            }
        }
        this.csvDataHandle.clear();
        this.csvMap.clear();
        this.csvCount = 0;
        this.minTime = 0;
        this.maxTime = 0;
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
     * @author Insomnia
     * 整体检测数据集输出路径
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

}
