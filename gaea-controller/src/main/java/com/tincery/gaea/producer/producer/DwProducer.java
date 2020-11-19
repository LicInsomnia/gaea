package com.tincery.gaea.producer.producer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;

@Component
@Slf4j
@Setter
public class DwProducer {

@Autowired
JmsMessagingTemplate jmsMessagingTemplate;

public void producer(Queue queue) {
    jmsMessagingTemplate.convertAndSend(queue, "苍叔牛逼");
    try {
        log.info("提交了一条dw.{}任务", queue.getQueueName());
    } catch (JMSException e) {
        e.printStackTrace();
    }
}

}
