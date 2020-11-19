package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.datawarehouse.CerJob;
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
public class DwQuartzConfig {


    private static final String PREFIX = "controller.model.datawarehouse";
    @Autowired
    private ControllerConfigProperties controllerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "reorganization")
    public JobDetail reorganizationJob() {
        log.info("控制器此次分发reorganization任务");
        return JobBuilder.newJob(ReorganizationJob.class)
                .withIdentity("reorganizationJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "reorganization")
    public Trigger dataWarehouseJobTrigger() {
        String cron = controllerConfigProperties.getDataWarehouse().getReorganization();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(reorganizationJob())
                .withIdentity("reorganizationJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "cer")
    public JobDetail cerJob() {
        log.info("控制器此次分发cer任务");
        return JobBuilder.newJob(CerJob.class)
                .withIdentity("cerJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "cer")
    public Trigger cerJobTrigger() {
        String cron = controllerConfigProperties.getDataWarehouse().getReorganization();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(cerJob())
                .withIdentity("cerJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
