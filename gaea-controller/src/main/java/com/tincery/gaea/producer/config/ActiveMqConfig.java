package com.tincery.gaea.producer.config;

import com.tincery.gaea.api.base.QueueNames;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;


/**
 * @author gxz gongxuanzhang@foxmail.com
 * ActiveMq 被注入的queue
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

    @Bean (name = QueueNames.SRC_SSL)
    public Queue getSrcSsl() {
        return new ActiveMQQueue(QueueNames.SRC_SSL);
    }

    @Bean (name = QueueNames.SRC_DNS)
    public Queue getSrcDns() {
        return new ActiveMQQueue(QueueNames.SRC_DNS);
    }

    @Bean (name = QueueNames.SRC_HTTP)
    public Queue getSrcHttp() {
        return new ActiveMQQueue(QueueNames.SRC_HTTP);
    }

    @Bean (name = QueueNames.SRC_EMAIL)
    public Queue getSrcEmail() {
        return new ActiveMQQueue(QueueNames.SRC_EMAIL);
    }

    @Bean (name = QueueNames.DW_REORGANIZATION)
    public Queue getDwReorganization() {
        return new ActiveMQQueue(QueueNames.DW_REORGANIZATION);
    }

}
