package com.tincery.gaea.producer.config;

import com.tincery.gaea.api.base.QueueNames;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import javax.jms.Queue;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
@EnableConfigurationProperties (ControllerConfigProperties.class)
public class SourceJob extends QuartzJobBean {

    @Resource (name = QueueNames.SRC_SESSION)
    private Queue sessionQueue;
    @Resource (name = QueueNames.SRC_IMPSESSION)
    private Queue impSessionQueue;
    @Resource (name = QueueNames.SRC_SSL)
    private Queue sslQueue;
    @Resource (name = QueueNames.SRC_DNS)
    private Queue dnsQueue;
    @Resource (name = QueueNames.SRC_EMAIL)
    private Queue emailQueue;
    @Autowired
    private SrcProducer srcProducer;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private ControllerConfigProperties controllerConfigProperties;


    @Override
    protected void executeInternal(JobExecutionContext context) {
        if (controllerConfigProperties.isEmail()) {
            this.srcProducer.producer(this.emailQueue, "email", ".dat");
        }

        this.srcProducer.producer(this.sessionQueue, "session", ".txt");
        this.srcProducer.producer(this.impSessionQueue, "impsession", ".txt");
        this.srcProducer.producer(this.sslQueue, "ssl", ".txt");
        this.srcProducer.producer(this.dnsQueue, "dns", ".txt");

    }
}
