package com.tincery.gaea.datamarket.sessionadjust.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.datamarket.sessionadjust.GaeaSessionAdjustApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SpringBootTest(classes = GaeaSessionAdjustApplication.class)
class SessionAdjustReceiverTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void bbb() throws JMSException {
        TextMessage activeMQMessage = new ActiveMQTextMessage();
        activeMQMessage.setText("D:\\data5\\datawarehouse\\json\\impsession\\impsession_0.json");
        receiver.receive(activeMQMessage);
    }


}
