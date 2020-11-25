package com.tincery.gaea.datamarket.alarmstatistic;

import com.tincery.gaea.core.base.component.Receiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SpringBootTest(classes = GaeaAlarmStatisticApplication.class)
class AssetReceiverTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void bbb() throws JMSException {
        TextMessage activeMQMessage = new ActiveMQTextMessage();
        activeMQMessage.setText("AlarmStatistic Test.");
        receiver.receive(activeMQMessage);
    }

}
