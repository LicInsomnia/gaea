package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.core.base.component.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class MessageListener {

    @Autowired
    private Receiver receiver;

    @JmsListener(destination = "src_espandah")
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println("接收到了内容");
        receiver.receive(textMessage);
    }
}
