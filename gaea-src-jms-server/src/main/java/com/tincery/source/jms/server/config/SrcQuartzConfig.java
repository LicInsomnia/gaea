package com.tincery.source.jms.server.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class SrcQuartzConfig {


    private final String cron = "0 0/1 * * * ?";


    @Bean
    public JobDetail reorganizationJob() {
        return JobBuilder.newJob(SendMessageJob.class)
                .withIdentity("reorganizationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger databaseMonitorJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(reorganizationJob())
                .withIdentity("reorganizationJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
