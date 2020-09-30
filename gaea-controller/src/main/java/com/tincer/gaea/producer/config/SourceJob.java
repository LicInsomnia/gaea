package com.tincer.gaea.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import javax.jms.Queue;

import static com.tincer.gaea.producer.config.QueueNames.SRC_SESSION;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class SourceJob extends QuartzJobBean {
    @Resource (name = SRC_SESSION)
    private Queue sessionQueue;
    @Autowired
    private SrcProducer srcProducer;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        this.srcProducer.producer(this.sessionQueue, "session", ".txt");
    }
}
