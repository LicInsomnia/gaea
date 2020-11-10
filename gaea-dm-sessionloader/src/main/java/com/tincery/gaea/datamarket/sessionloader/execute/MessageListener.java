package com.tincery.gaea.datamarket.sessionloader.execute;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.core.base.component.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public class MessageListener {
    @Autowired
    private Receiver receiver;

    @JmsListener(destination = QueueNames.DM_SESSION_LOADER)
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println("接收到了内容");
        receiver.receive(textMessage);
    }
}
