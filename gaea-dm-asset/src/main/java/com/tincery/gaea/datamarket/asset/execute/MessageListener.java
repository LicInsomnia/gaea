package com.tincery.gaea.datamarket.asset.execute;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.core.base.component.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class MessageListener {
    @Autowired
    private Receiver receiver;

    @JmsListener(destination = QueueNames.DM_ASSET)
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println("接收到了内容");
        receiver.receive(textMessage);
    }
}
