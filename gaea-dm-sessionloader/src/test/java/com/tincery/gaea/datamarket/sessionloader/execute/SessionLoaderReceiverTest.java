package com.tincery.gaea.datamarket.sessionloader.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.datamarket.sessionloader.GaeaSessionLoaderApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SpringBootTest(classes = GaeaSessionLoaderApplication.class)
class SessionLoaderReceiverTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void bbb() throws JMSException {
        TextMessage activeMQMessage = new ActiveMQTextMessage();
        activeMQMessage.setText("D:\\data5\\datawarehouse\\json\\impsession\\impsession_0.json");
        receiver.receive(activeMQMessage);
    }


}
