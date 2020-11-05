package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.datawarehouse.ReorganizationJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
@Slf4j
public class OdsQuartzConfig {


    private static final String PREFIX = "controller.model.ods";
    @Autowired
    private ControllerConfigProperties controllerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "httpanalysis")
    public JobDetail httpAnalysisJob() {
        log.info("控制器此次分发httpanalysis任务");
        return JobBuilder.newJob(ReorganizationJob.class)
                .withIdentity("httpanalysisJob")
                .storeDurably()
                .build();
    }


    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "httpanalysis")
    public Trigger dataWarehouseJobTrigger() {
        String cron = controllerConfigProperties.getOds().getHttpanalysis();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(httpAnalysisJob())
                .withIdentity("httpanalysisJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
