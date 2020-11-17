package com.tincery.gaea.datamarket.asset.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.datamarket.asset.GaeaAssetApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SpringBootTest(classes = GaeaAssetApplication.class)

class AssetReceiverTest {

    @Autowired
    private Receiver receiver;
    @Test
    public void bbb() throws JMSException {
        TextMessage activeMQMessage = new ActiveMQTextMessage();
        activeMQMessage.setText("D:\\gaeaData\\asset_1605489600012.json");
        receiver.receive(activeMQMessage);
    }

}
