package com.tincery.gaea.source.ssl.execute;

import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.SslData;
import com.tincery.gaea.core.src.AbstractCollectExecute;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerChain;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.source.ssl.config.property.SslProperties;
import com.tincery.starter.base.mgt.NodeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author gxz
 */
@Component
@Slf4j
@Setter
@Getter
public class SslExecute extends AbstractCollectExecute<SslProperties, SslData> {


    @Autowired
    private ImpTargetSetupDao impTargetSetupDao;

    @Autowired
    private PayloadDetector payloadDetector;

    @Autowired
    private ApplicationProtocol applicationProtocol;

    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    private PassRule passRule;


    @Autowired
    public void setAnalysis(SslLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Override
    @Autowired
    public void setCerChain(CerChain cerChain) {
        this.cerChain = cerChain;
    }


    @Override
    public String getHead() {
        return HeadConst.SSL_HEADER;
    }


    @Override
    protected void putCsvMap(SslData sslData) {
        //解析证书链 添加证书链的速度过快 不建议用并发集合执行
        synchronized (this) {
            this.cerChain.append(sslData.getCerChain(), sslData.getCapTime());
        }
        sslData.adjust();
        if (RuleRegistry.getInstance().matchLoop(sslData)) {
            // 过滤规则  其中alarm规则是有同步块的
            return;
        }
        appendCsvData(sslData.getDateSetFileName(NodeInfo.getCategory()),
                sslData.toCsv(CSV_SEPARATOR),
                sslData.getCapTime()
        );
    }


    @Override
    public long multiThreadExecute(List<String> lines) {
        List<List<String>> parts = Lists.partition(lines, lines.size() / properties.getExecutor());
        int cpu = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                cpu + 1,
                cpu * 2,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        CountDownLatch countDownLatch = new CountDownLatch(parts.size());
        for (List<String> part : parts) {
            executorService.execute(new SslProducer(countDownLatch, part));
        }
        try {
            countDownLatch.await();
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.MINUTES);
            return Instant.now().toEpochMilli();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Instant.now().toEpochMilli();
        } finally {
            executorService.shutdownNow();
        }
    }


    @Override
    public void init() {
        super.init();
        // TODO: 2020/9/2  初始化有一个IP内容
        offLineIfNecessary();
        RuleRegistry.getInstance().putRule(alarmRule).putRule(passRule);
    }


    @Override
    @Autowired
    public void setProperties(SslProperties properties) {
        this.properties = properties;

    }

    private void offLineIfNecessary() {
        if (isOffLine()) {
            System.out.println("这里是离线内容");
            // @todo 这里是离线内容
           /* this.outputTimeSpace = 5;
            Map<String, Object> offLineConfiguration = (Map<String, Object>) this.config.getComConfig().get("offline");
            this.config.setSrcPath(offLineConfiguration.get("srcpath").toString() + "/" + this.remarkSource + "_txt");
            this.config.setDataPath(offLineConfiguration.get("offlinedatapath").toString() + "/" + this.remarkSource + "_data");*/
        }
    }


    public class SslProducer implements Runnable {

        CountDownLatch countDownLatch;

        List<String> lines;

        public SslProducer(CountDownLatch countDownLatch, List<String> lines) {
            this.countDownLatch = countDownLatch;
            this.lines = lines;
        }

        @Override
        public void run() {
            analysisLine(this.lines);
            log.info("生产者线程{}执行结束", Thread.currentThread().getName());
            countDownLatch.countDown();
        }
    }


}
