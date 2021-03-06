package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.core.base.component.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author liuming
 */
@Component
public class MessageListener {

    @Autowired
    private Receiver receiver;

    @JmsListener(destination = QueueNames.DW_CER)
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println("接收到了内容:" + textMessage.getText());
        receiver.receive(textMessage);
    }
}
