package com.tincery.gaea.producer.job.support;

import com.tincery.gaea.api.base.QueueNames;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import javax.jms.Queue;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class MongoStashJob extends QuartzJobBean {
    @Resource(name = QueueNames.SUPPORT_MONGO_STASH)
    private Queue mongoQueue;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;


    @Override
    protected void executeInternal(JobExecutionContext context) {
        jmsMessagingTemplate.convertAndSend(mongoQueue,"mongoStash干活了");
        log.info("给mongoStash发送了一条消息");

    }
}
