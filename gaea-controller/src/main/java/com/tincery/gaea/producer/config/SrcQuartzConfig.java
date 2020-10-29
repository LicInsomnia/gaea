package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.src.*;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(prefix = PREFIX, name = "flow")
    public JobDetail flowJob() {
        return JobBuilder.newJob(FlowJob.class)
                .withIdentity("flowJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "flow")
    public Trigger flowJobTrigger() {
        String cron = controllerConfigProperties.getSource().getFlow();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sessionJob())
                .withIdentity("flowJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "session")
    public JobDetail sessionJob() {
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("sessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "session")
    public Trigger sessionJobTrigger() {
        String cron = controllerConfigProperties.getSource().getSession();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sessionJob())
                .withIdentity("sessionJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "impsession")
    public JobDetail impsessionJob() {
        return JobBuilder.newJob(ImpSessionJob.class)
                .withIdentity("impsessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "impsession")
    public Trigger impsessionJobTrigger() {
        String cron = controllerConfigProperties.getSource().getImpSession();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(impsessionJob())
                .withIdentity("impsessionJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "http")
    public JobDetail httpJob() {
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("httpJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "http")
    public Trigger httpJobTrigger() {
        String cron = controllerConfigProperties.getSource().getHttp();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(httpJob())
                .withIdentity("httpJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "dns")
    public JobDetail dnsJob() {
        return JobBuilder.newJob(DnsJob.class)
                .withIdentity("dnsJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "dns")
    public Trigger dnsJobTrigger() {
        String cron = controllerConfigProperties.getSource().getDns();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(dnsJob())
                .withIdentity("dnsJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssl")
    public JobDetail sslJob() {
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("sslJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssl")
    public Trigger sslJobTrigger() {
        String cron = controllerConfigProperties.getSource().getSsl();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sslJob())
                .withIdentity("sslJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "openvpn")
    public JobDetail openVpnJob() {
        return JobBuilder.newJob(OpenVpnJob.class)
                .withIdentity("openVpnJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "openVpn")
    public Trigger openVpnJobTrigger() {
        String cron = controllerConfigProperties.getSource().getOpenVpn();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sslJob())
                .withIdentity("openVpnJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "email")
    public JobDetail emailJob() {
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("emailJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "email")
    public Trigger emailJobTrigger() {
        String cron = controllerConfigProperties.getSource().getEmail();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(emailJob())
                .withIdentity("emailJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssh")
    public JobDetail sshJob() {
        return JobBuilder.newJob(SshJob.class)
                .withIdentity("sshJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssh")
    public Trigger sshJobTrigger() {
        String cron = controllerConfigProperties.getSource().getSsh();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sshJob())
                .withIdentity("sshJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
