package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.datamarket.AlarmCombineJob;
import com.tincery.gaea.producer.job.datawarehouse.ReorganizationJob;
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


    private static final String PREFIX = "controller.model.dm";
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
        String cron = controllerConfigProperties.getDm().getAlarmcombine();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(alarmcombineJob())
                .withIdentity("alarmcombineJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
