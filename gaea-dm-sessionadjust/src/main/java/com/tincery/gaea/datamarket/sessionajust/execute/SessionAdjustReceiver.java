package com.tincery.gaea.datamarket.sessionajust.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;

@Slf4j
@Component
public class SessionAdjustReceiver implements Receiver {

    @Autowired
    private DnsRequest dnsRequest;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
    }

    @Override
    public void init() {

    }
}
