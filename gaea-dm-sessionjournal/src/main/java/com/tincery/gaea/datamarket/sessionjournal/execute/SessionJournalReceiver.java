package com.tincery.gaea.datamarket.sessionjournal.execute;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Insomnia
 */
@Slf4j
@Component
public class SessionJournalReceiver extends AbstractDataMarketReceiver {

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

    @Autowired
    @Override
    protected void setDmProperties(DmProperties dmProperties) {
        this.dmProperties = dmProperties;
    }

    @Override
    protected void dmFileAnalysis(File file) {
        List<String> allLines = FileUtils.readLine(file);
        log.info("开始解析重点目标记录，本次处理共获取重点目标记录[{}]条", allLines.size());
        int executor = this.dmProperties.getExecutor();
        List<SessionMergeData> sessionMergeDataList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        if (executor <= 1) {
            log.info("单线程执行");
            for (String line : allLines) {
                try {
                    if (line.isEmpty()) {
                        continue;
                    }
                    SessionMergeData data = JSON.parseObject(line, SessionMergeData.class);
                    if (null == data) {
                        log.error("无法解析Json序列化的记录：{}", line);
                        continue;
                    }
                } catch (Exception e) {
                    log.error("异常错误的记录：{}", line);
                }
            }
        } else {
            log.info("多线程执行，线程数：[{}]", executor);
            List<List<String>> partitions = Lists.partition(allLines, allLines.size() / executor + 1);
            List<Future<List<SessionMergeData>>> futures = new ArrayList<>();
            for (List<String> lines : partitions) {
//                futures.add(executorService.submit(new SessionMergeProducer(lines, this.sessionAdjustFactory)));
            }
            for (Future<List<SessionMergeData>> future : futures) {
                try {
                    sessionMergeDataList.addAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("重点目标会话处理完成，成功解析记录：[{}/{}]，用时：{}ms",
                sessionMergeDataList.size(), allLines.size(), (System.currentTimeMillis() - startTime));
        output(sessionMergeDataList);
    }

    @Override
    public void init() {

    }

    private void output(List<SessionMergeData> list) {
        String fileName = NodeInfo.getSessionAdjustPath()
                + "sessionAdjust_" + System.currentTimeMillis() + ".json";
        FileWriter fileWriter = new FileWriter(fileName);
        for (SessionMergeData sessionMergeData : list) {
            fileWriter.write(JSON.toJSONString(sessionMergeData));
        }
        fileWriter.close();
    }

}
