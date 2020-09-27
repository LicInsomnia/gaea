package com.tincery.gaea.source.session.config;

import com.tincery.gaea.source.session.quartz.SessionJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gongxuanzhang
 */
@Configuration
public class QuartzConfig {

    private final String cron = "0 0/1 * * * ?";


    @Bean
    public JobDetail sessionJob() {
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("sessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger databaseMonitorJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sessionJob())
                .withIdentity("sessionJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
