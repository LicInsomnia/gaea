package com.tincery.dw.commomappdetect;

import com.tincery.dw.commomappdetect.execute.CommonSearchReceiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.MessageNotWriteableException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = CommonAppdetectApplication.class)
class CommonAppdetectApplicationTest {

    @Autowired
    private CommonSearchReceiver commonSearchReceiver;

    @Test
    public void aa() throws MessageNotWriteableException {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("asdf");
        commonSearchReceiver.receive(activeMQTextMessage);
    }

}
