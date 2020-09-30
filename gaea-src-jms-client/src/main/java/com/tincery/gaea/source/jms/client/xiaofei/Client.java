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
    @JmsListener (destination = "src_session")
    public void receive(TextMessage textMessage) throws JMSException {
        String text = textMessage.getText();
        System.out.println("接收到的消息是[" + text + "]");
        System.out.println(LocalDateTime.now() + "处理消息");
    }
}
