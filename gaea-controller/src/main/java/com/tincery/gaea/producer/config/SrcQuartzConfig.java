package com.tincery.gaea.producer.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class SrcQuartzConfig {


    private final String cron = "0/3 * * * * ?";


    @Bean
    public JobDetail sourceJob() {
        return JobBuilder.newJob(SourceJob.class)
                .withIdentity("srcJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger databaseMonitorJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sourceJob())
                .withIdentity("srcJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
