package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.src.*;
import com.tincery.gaea.producer.job.support.MongoStashJob;
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
public class SupportQuartzConfig {

    private static final String PREFIX = "controller.model.support";

    @Autowired
    private ControllerConfigProperties controllerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "mongo-stash")
    public JobDetail mongoStashJob() {
        log.info("控制器此次分发mongoStash任务");
        return JobBuilder.newJob(MongoStashJob.class)
                .withIdentity("mongoStashJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "mongo-stash")
    public Trigger mongoStashJobTrigger() {
        String cron = controllerConfigProperties.getSupport().getMongoStash();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(mongoStashJob())
                .withIdentity("mongoStashJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }


}
