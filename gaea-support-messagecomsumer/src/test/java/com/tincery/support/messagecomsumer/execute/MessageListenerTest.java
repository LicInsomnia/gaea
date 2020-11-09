package com.tincery.support.messagecomsumer.execute;

import com.tincery.support.messagecomsumer.MessageConsumerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = MessageConsumerApplication.class)
class MessageListenerTest {

    @Autowired
    private MessageListener messageListener;

    @Test
    public void aa(){

    }

}
