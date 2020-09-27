package com.tincery.gaea.source.session.execute;

import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.src.AbstractCollectExecute;
import com.tincery.gaea.source.session.config.property.SessionProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
@Service
public class SessionExecute extends AbstractCollectExecute<SessionProperties, SessionData> {


    @Autowired
    private ImpTargetSetupDao impTargetSetupDao;

    @Autowired
    private PassRule passrule;

    @Autowired
    private PayloadDetector payloadDetector;


    @Autowired
    public void setAnalysis(SessionLineAnalysis analysis) {
        this.analysis = analysis;
    }


    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
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
            executorService.execute(new SessionProducer(countDownLatch, part));
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
        // loadGroup();
        offLineIfNecessary();
        registryRules(passrule);
    }

    public void registryRules(PassRule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

    @Override
    @Autowired
    public void setProperties(SessionProperties properties) {
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

    /*private void loadGroup() {
        List<ImpTargetSetupDO> activityData = impTargetSetupDao.getActivityData();
        activityData.stream()
                .filter(impTargetSetupDO -> StringUtils.notAllowNull(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()))
                .forEach((impTargetSetupDO) -> this.target2Group.put(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()));
        log.info("加载了{}组  目标配置", this.target2Group.size());
    }*/


    public class SessionProducer implements Runnable {

        CountDownLatch countDownLatch;

        List<String> lines;


        public SessionProducer(CountDownLatch countDownLatch, List<String> lines) {
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
