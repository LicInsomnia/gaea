package com.tincery.gaea.datamarket.sessionajust.execute;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class SessionAdjustReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private DnsRequest dnsRequest;

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

    @Override
    protected void setDmProperties(DmProperties dmProperties) {
        this.dmProperties = dmProperties;
    }

    @Override
    protected void dmFileAnalysis(List<String> allLines) {
        int executor = this.dmProperties.getExecutor();
        List<SessionMergeData> sessionMergeDataList = new ArrayList<>();
        if (executor <= 1) {
            // 单线程执行
            for (String line : allLines) {
                AbstractDataWarehouseData data = JSON.parseObject(line, AbstractDataWarehouseData.class);
                sessionMergeDataList.add(SessionMergeProducer.producer(data));
            }
        } else {
            // 多线程执行
            List<List<String>> partitions = Lists.partition(allLines, allLines.size() / executor + 1);
            List<Future<SessionMergeResult>> futures = new ArrayList<>();
            for (List<String> lines : partitions) {
                futures.add(executorService.submit(new SessionMergeProducer(lines)));
            }
            for (Future<SessionMergeResult> future : futures) {
                try {
                    sessionMergeDataList.addAll(future.get().list);
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
    private static class SessionMergeProducer implements Callable<SessionMergeResult> {

        final private List<String> lines;

        public SessionMergeProducer(List<String> lines) {
            this.lines = lines;
        }

        public static SessionMergeData producer(AbstractDataWarehouseData data) {
            return new SessionMergeData();
        }

        @Override
        public SessionMergeResult call() {
            List<SessionMergeData> list = new ArrayList<>();
            for (String line : lines) {
                AbstractDataWarehouseData data = JSON.parseObject(line, AbstractDataWarehouseData.class);
                list.add(producer(data));
            }
            return new SessionMergeResult(list);
        }

    }

    /**
     * @author Insomnia
     */
    private static class SessionMergeResult {

        private final List<SessionMergeData> list;

        public SessionMergeResult(List<SessionMergeData> list) {
            this.list = list;
        }

    }

}
