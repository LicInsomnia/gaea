package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerSelector;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.dw.SessionFactory;
import com.tincery.starter.base.InitializationRequired;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class ReorganizationFactory implements InitializationRequired {

    private final IpSelector ipSelector;
    private final CerSelector cerSelector;
    private final ApplicationProtocol applicationProtocol;
    private final DnsRequest dnsRequest;

    private SessionFactory sessionFactory;

    public ReorganizationFactory(IpSelector ipSelector, CerSelector cerSelector, ApplicationProtocol applicationProtocol, DnsRequest dnsRequest) {
        this.ipSelector = ipSelector;
        this.cerSelector = cerSelector;
        this.applicationProtocol = applicationProtocol;
        this.dnsRequest = dnsRequest;
    }

    public void sessionFactoryInit() {
        this.sessionFactory = new SessionFactory();
    }

    @Override
    public void init() {

    }

}
