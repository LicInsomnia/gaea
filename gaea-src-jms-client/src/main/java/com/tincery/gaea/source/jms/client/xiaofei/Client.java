package com.tincery.gaea.source.jms.client.xiaofei;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.LocalDateTime;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class Client {
    @JmsListener(destination = "aaa")
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println(LocalDateTime.now()+"处理消息");
    }
}
