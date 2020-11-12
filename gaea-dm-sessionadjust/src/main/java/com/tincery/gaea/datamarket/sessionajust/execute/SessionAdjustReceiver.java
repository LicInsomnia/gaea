package com.tincery.gaea.datamarket.sessionajust.execute;

import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

@Slf4j
@Component
public class SessionAdjustReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private DnsRequest dnsRequest;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
    }

    @Override
    protected void dmFileAnalysis(List<String> lines) {

    }

    @Override
    protected void setDmProperties(DmProperties dmProperties) {

    }

    @Override
    public void init() {

    }
}
