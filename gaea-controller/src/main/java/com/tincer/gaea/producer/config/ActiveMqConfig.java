package com.tincer.gaea.producer.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

import static com.tincer.gaea.producer.config.QueueNames.SRC_IMPSESSION;
import static com.tincer.gaea.producer.config.QueueNames.SRC_SESSION;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class ActiveMqConfig {


    @Bean (name = SRC_SESSION)
    public Queue getSrcSessionQueue() {
        return new ActiveMQQueue(SRC_SESSION);
    }

    @Bean (name = SRC_IMPSESSION)
    public Queue getSrcImpSession() {
        return new ActiveMQQueue(SRC_IMPSESSION);
    }

}