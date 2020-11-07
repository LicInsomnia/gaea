package com.tincery.gaea.producer.producer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.File;

@Component
@Slf4j
@Setter
public class DmProducer {

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${node.data-path}")
    private String dataPath;

    public void producer(Queue queue) {
        File path = new File(dataPath + "/datawarehouse/json/alarmMaterial");
        if (!path.exists()) {
            log.info("扫描路径{}不存在", path.getAbsoluteFile());
            return;
        }
        jmsMessagingTemplate.convertAndSend(queue, path.getAbsoluteFile());
        try {
            log.info("提交了一条dm.{}任务", queue.getQueueName());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
