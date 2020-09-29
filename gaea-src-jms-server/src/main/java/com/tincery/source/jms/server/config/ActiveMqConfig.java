package com.tincery.source.jms.server.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
public class ActiveMqConfig {

    @Bean
    public Queue aa(){
        return new ActiveMQQueue("aaa");
    }
}
