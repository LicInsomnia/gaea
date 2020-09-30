package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.src.DnsJob;
import com.tincery.gaea.producer.job.src.ImpSessionJob;
import com.tincery.gaea.producer.job.src.SessionJob;
import com.tincery.gaea.producer.job.src.SslJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class SrcQuartzConfig {

    private static final String PREFIX = "controller.model.src";

    @Autowired
    private ControllerConfigProperties controllerConfigProperties;


    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "session")
    public JobDetail sessionJob() {
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("sessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "session")
    public Trigger sessionJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getSession();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sessionJob())
                .withIdentity("sessionJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "impsession")
    public JobDetail impsessionJob() {
        return JobBuilder.newJob(ImpSessionJob.class)
                .withIdentity("impsessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "impsession")
    public Trigger impsessionJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getImpsession();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(impsessionJob())
                .withIdentity("impsessionJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "http")
    public JobDetail httpJob() {
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("httpJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "http")
    public Trigger httpJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getHttp();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(httpJob())
                .withIdentity("httpJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "dns")
    public JobDetail dnsJob() {
        return JobBuilder.newJob(DnsJob.class)
                .withIdentity("dnsJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "dns")
    public Trigger dnsJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getDns();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(dnsJob())
                .withIdentity("dnsJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "ssl")
    public JobDetail sslJob() {
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("sslJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "ssl")
    public Trigger sslJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getSsl();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sslJob())
                .withIdentity("sslJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "email")
    public JobDetail emailJob() {
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("emailJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX,name = "email")
    public Trigger emailJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getEmail();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(emailJob())
                .withIdentity("emailJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
