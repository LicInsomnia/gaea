package com.tincery.gaea.datamarket.sessionjournal;

import com.tincery.gaea.core.base.component.Receiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SpringBootTest(classes = GaeaSessionJournalApplication.class)
class SessionJournalReceiverTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void aa() throws JMSException {
        TextMessage activeMQMessage = new ActiveMQTextMessage();
        activeMQMessage.setText("D:\\data5\\datawarehouse\\json\\sessionAdjust\\sessionAdjust_0.json");
        receiver.receive(activeMQMessage);
    }

}
