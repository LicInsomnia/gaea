package com.tincery.gaea.datamarket.sessionadjust.execute;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.tool.util.FileUtils;
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
public class SessionAdjustReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private SessionAdjustFactory sessionAdjustFactory;

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
        int executor = this.dmProperties.getExecutor();
        List<SessionMergeData> sessionMergeDataList = new ArrayList<>();
        if (executor <= 1) {
            // 单线程执行
            for (String line : allLines) {
                try {
                    AbstractDataWarehouseData data = JSON.parseObject(line, AbstractDataWarehouseData.class);
                    sessionMergeDataList.add(this.sessionAdjustFactory.adjustSessionData(data));
                } catch (Exception e) {
                    log.error("错误ImportantSession：{}", line);
                }
            }
        } else {
            // 多线程执行
            List<List<String>> partitions = Lists.partition(allLines, allLines.size() / executor + 1);
            List<Future<List<SessionMergeData>>> futures = new ArrayList<>();
            for (List<String> lines : partitions) {
                futures.add(executorService.submit(new SessionMergeProducer(lines, this.sessionAdjustFactory)));
            }
            for (Future<List<SessionMergeData>> future : futures) {
                try {
                    sessionMergeDataList.addAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        output(sessionMergeDataList);
    }

    @Override
    public void init() {

    }

    private void output(List<SessionMergeData> list) {

    }

    /**
     * @author Insomnia
     */
    private static class SessionMergeProducer implements Callable<List<SessionMergeData>> {

        private final List<String> lines;
        private final SessionAdjustFactory sessionAdjustFactory;

        public SessionMergeProducer(List<String> lines, SessionAdjustFactory sessionAdjustFactory) {
            this.lines = lines;
            this.sessionAdjustFactory = sessionAdjustFactory;
        }

        @Override
        public List<SessionMergeData> call() {
            List<SessionMergeData> list = new ArrayList<>();
            for (String line : lines) {
                try {
                    AbstractDataWarehouseData data = JSON.parseObject(line, AbstractDataWarehouseData.class);
                    list.add(sessionAdjustFactory.adjustSessionData(data));
                } catch (Exception e) {
                    log.error("错误ImportantSession：{}", line);
                }
            }
            return list;
        }
    }


}
