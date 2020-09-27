package com.tincery.gaea.source.session.execute;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerChain;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.src.AbstractCollectExecute;
import com.tincery.gaea.source.session.config.property.EmailProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class EmailExecute extends AbstractCollectExecute<EmailProperties, EmailData> {


    private boolean emailSuffixAlarm;

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
    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Autowired
    public void setAnalysis(EmailLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Override
    @Autowired
    public void setCerChain(CerChain cerChain) {
        this.cerChain = cerChain;
    }


    @Override
    public String getHead() {
        return HeadConst.EMAIL_HEADER;
    }


    @Override
    protected void putCsvMap(EmailData emailData) {
        List<EmailData> split = emailData.split();
        for (EmailData email : split) {
            if (RuleRegistry.getInstance().matchLoop(email)) {
                return;
            }

            appendCsvData(emailData.getDateSetFileName(NodeInfo.getCategory()),
                    emailData.toCsv(CSV_SEPARATOR),
                    emailData.getCapTime()
            );
        }
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
        // TODO: 2020/9/2  初始化有一个IP内容
        offLineIfNecessary();
        RuleRegistry ruleRegistry = RuleRegistry.getInstance();
        ruleRegistry.putRule(passRule);
        // 如果这里需要告警 才加入  ruleRegistry.putRule(alarmRule);
        JSONObject emailInfo = (JSONObject) JSONObject.toJSON(CommonConfig.get("email"));
        this.emailSuffixAlarm = emailInfo.getBoolean("activity");
        // TODO: 2020/9/11 如果emailSuffixAlarm这个为true 还需要加载一个信息
    }


    @Override
    @Autowired
    public void setProperties(EmailProperties properties) {
        this.properties = properties;

    }

    private void offLineIfNecessary() {
        if (isOffLine()) {
            System.out.println("这里是离线内容");
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
