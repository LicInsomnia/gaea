package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.datawarehouse.ReorganizationJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class DwQuartzConfig {


   @Autowired
   private ControllerConfigProperties controllerConfigProperties;

    private static final String PREFIX = "controller.model.datawarehouse";

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "reorganization")
    public JobDetail reorganizationJob() {
        return JobBuilder.newJob(ReorganizationJob.class)
                .withIdentity("reorganizationJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "reorganization")
    public Trigger dataWarehouseJobTrigger() {
        String cron = controllerConfigProperties.getDatawarehouse().getReorganization();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(reorganizationJob())
                .withIdentity("reorganizationJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
