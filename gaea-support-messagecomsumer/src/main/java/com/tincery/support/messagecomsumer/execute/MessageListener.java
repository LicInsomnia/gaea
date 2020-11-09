package com.tincery.support.messagecomsumer.execute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void aa() throws IllegalAccessException {
    }


}
