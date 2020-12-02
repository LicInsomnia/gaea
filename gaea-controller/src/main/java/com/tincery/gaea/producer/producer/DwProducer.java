package com.tincery.gaea.producer.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.File;

/**
 * @author gongxuanzhang
 */
@Component
@Slf4j
public class DwProducer extends AbstractProducer {

    @Override
    public void producer(Queue queue, String category, String extension) {
        jmsMessagingTemplate.convertAndSend(queue, "dw任务");
        try {
            log.info("提交了一条dw.{}任务", queue.getQueueName());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getRootFile(String category, String extension) {
        return null;
    }

    @Override
    @Autowired
    public void setJmsMessagingTemplate(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }
}
