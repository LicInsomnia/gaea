package com.tincery.gaea.producer.config;

import com.tincery.gaea.producer.job.src.*;
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
public class SrcQuartzConfig {

    private static final String PREFIX = "controller.model.src";

    @Autowired
    private ControllerConfigProperties controllerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "flow")
    public JobDetail flowJob() {
        log.info("控制器此次分发flow任务");
        return JobBuilder.newJob(FlowJob.class)
                .withIdentity("flowJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "flow")
    public Trigger flowJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getFlow();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(flowJob())
                .withIdentity("flowJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "session")
    public JobDetail sessionJob() {
        log.info("控制器此次分发session任务");
        return JobBuilder.newJob(SessionJob.class)
                .withIdentity("sessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "session")
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
    @ConditionalOnProperty(prefix = PREFIX, name = "impsession")
    public JobDetail impsessionJob() {
        log.info("控制器此次分发impsession任务");
        return JobBuilder.newJob(ImpSessionJob.class)
                .withIdentity("impsessionJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "impsession")
    public Trigger impsessionJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getImpSession();
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
        log.info("控制器此次分发http任务");
        return JobBuilder.newJob(HttpJob.class)
                .withIdentity("httpJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "http")
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
    @ConditionalOnProperty(prefix = PREFIX, name = "dns")
    public JobDetail dnsJob() {
        log.info("控制器此次分发dns任务");
        return JobBuilder.newJob(DnsJob.class)
                .withIdentity("dnsJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "dns")
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
    @ConditionalOnProperty(prefix = PREFIX, name = "ssl")
    public JobDetail sslJob() {
        log.info("控制器此次分发ssl任务");
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("sslJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssl")
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
    @ConditionalOnProperty(prefix = PREFIX, name = "openvpn")
    public JobDetail openVpnJob() {
        log.info("控制器此次分发openvpn任务");
        return JobBuilder.newJob(OpenVpnJob.class)
                .withIdentity("openVpnJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "openvpn")
    public Trigger openVpnJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getOpenVpn();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(openVpnJob())
                .withIdentity("openVpnJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "email")
    public JobDetail emailJob() {
        log.info("控制器此次分发email任务");
        return JobBuilder.newJob(SslJob.class)
                .withIdentity("emailJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "email")
    public Trigger emailJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getEmail();
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
        log.info("控制器此次分发ssh任务");
        return JobBuilder.newJob(SshJob.class)
                .withIdentity("sshJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ssh")
    public Trigger sshJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getSsh();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(sshJob())
                .withIdentity("sshJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "isakmp")
    public JobDetail isakmpJob() {
        log.info("控制器此次分发isakmp任务");
        return JobBuilder.newJob(IsakmpJob.class)
                .withIdentity("isakmpJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "isakmp")
    public Trigger isakmpJobTrigger() {

        String cron = controllerConfigProperties.getSrc().getIsakmp();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(isakmpJob())
                .withIdentity("isakmpJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "pptpandl2tp")
    public JobDetail pptpandl2tpJob() {
        log.info("控制器此次分发pptpandl2tp任务");
        return JobBuilder.newJob(Pptpandl2tpJob.class)
                .withIdentity("pptpandl2tpJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "pptpandl2tp")
    public Trigger pptpandl2tpJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getPptpandl2tp();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(pptpandl2tpJob())
                .withIdentity("pptpandl2tpJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "espandah")
    public JobDetail espAndAhJob() {
        log.info("控制器此次分发espandah任务");
        return JobBuilder.newJob(EspAndAhJob.class)
                .withIdentity("espandahJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "espandah")
    public Trigger espAndAhJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getEspandah();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(espAndAhJob())
                .withIdentity("espandahJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "wechat")
    public JobDetail weChatJob() {
        log.info("控制器此次分发wechat任务");
        return JobBuilder.newJob(WeChatJob.class)
                .withIdentity("weChatJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "wechat")
    public Trigger weChatJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getWechat();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(weChatJob())
                .withIdentity("weChatJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ftpandtelnet")
    public JobDetail ftpandtelnetJob() {
        log.info("控制器此次分发ftpandtelnet任务");
        return JobBuilder.newJob(FtpandtelnetJob.class)
                .withIdentity("ftpandtelnetJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "ftpandtelnet")
    public Trigger ftpandtelnetJobTrigger() {
        String cron = controllerConfigProperties.getSrc().getFtpandtelnet();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(ftpandtelnetJob())
                .withIdentity("ftpandtelnetJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
