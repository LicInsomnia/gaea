package com.tincery.gaea.producer.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class DwQuartzConfig {


    private final String cron = "0/10 * * * * ?";


    @Bean
    public JobDetail dataWarehouseJob() {
        return JobBuilder.newJob(DataWarehouseJob.class)
                .withIdentity("reorganizationJob")
                .storeDurably()
                .build();
    }


    @Bean
    public Trigger dataWarehouseJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(dataWarehouseJob())
                .withIdentity("reorganizationJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
