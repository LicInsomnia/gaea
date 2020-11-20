package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.datamarket.AlarmCombineJob;
import com.tincery.gaea.producer.job.datamarket.AssetJob;
import com.tincery.gaea.producer.job.datamarket.SessionAdjustJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
@Slf4j
public class DmQuartzConfig {


    private static final String PREFIX = "controller.model.datamarket";
    @Autowired
    private ControllerConfigProperties controllerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "alarmcombine")
    public JobDetail alarmcombineJob() {
        log.info("控制器此次分发alarmcombine任务");
        return JobBuilder.newJob(AlarmCombineJob.class)
                .withIdentity("alarmcombineJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "alarmcombine")
    public Trigger alarmcombineJobTrigger() {
        String cron = controllerConfigProperties.getDatamarket().getAlarmCombine();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(alarmcombineJob())
                .withIdentity("alarmcombineJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "asset")
    public JobDetail assetJob() {
        log.info("控制器此次分发asset任务");
        return JobBuilder.newJob(AssetJob.class)
                .withIdentity("assetJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "asset")
    public Trigger assetJobTrigger() {
        String cron = controllerConfigProperties.getDatamarket().getAsset();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(assetJob())
                .withIdentity("assetJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "sessionadjust")
    public JobDetail sessionAdjustJob() {
        log.info("控制器此次分发sessionAdjust任务");
        return JobBuilder.newJob(SessionAdjustJob.class)
                .withIdentity("sessionAdjustJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "sessionadjust")
    public Trigger sessionAdjustJobTrigger() {
        String cron = controllerConfigProperties.getDatamarket().getSessionAdjust();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sessionAdjustJob())
                .withIdentity("sessionAdjustJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
