package com.tincery.gaea.datamarket.sessionajust.execute;


import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionFactory {

    @Autowired
    private DnsRequest dnsRequest;


    public SessionMergeData adjustSessionData(AbstractDataWarehouseData data) {
        return new SessionMergeData();
    }

}
