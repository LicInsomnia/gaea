package com.tincer.gaea.producer.config;

import com.tincery.gaea.api.base.QueueNames;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;



/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class ActiveMqConfig {


    @Bean (name = QueueNames.SRC_SESSION)
    public Queue getSrcSessionQueue() {
        return new ActiveMQQueue(QueueNames.SRC_SESSION);
    }

    @Bean (name = QueueNames.SRC_IMPSESSION)
    public Queue getSrcImpSession() {
        return new ActiveMQQueue(QueueNames.SRC_IMPSESSION);
    }

}
