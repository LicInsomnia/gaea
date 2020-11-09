package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.source.impsession.GaeaSourceImpSessionApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = GaeaSourceImpSessionApplication.class)
class MessageListenerTest {

    @Autowired
    private Receiver receiver;

    @Test
    public void aa(){

    }

}
