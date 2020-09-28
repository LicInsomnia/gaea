package com.tincery.gaea.datawarehouse.reorganization.config;

import com.tincery.gaea.datawarehouse.reorganization.job.ReorganizationJob;
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
    public JobDetail reorganizationJob() {
        return JobBuilder.newJob(ReorganizationJob.class)
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
