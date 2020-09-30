package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerSelector;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.dw.SessionFactory;
import com.tincery.starter.base.InitializationRequired;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
@Getter
@Component
public class ReorganizationFactory implements InitializationRequired {

    private final IpSelector ipSelector;
    private final CerSelector cerSelector;
    private final ApplicationProtocol applicationProtocol;
    private final DnsRequest dnsRequest;

@Value("${aa}")
private String aa;
    private SessionFactory sessionFactory;

    public ReorganizationFactory(IpSelector ipSelector, CerSelector cerSelector, ApplicationProtocol applicationProtocol, DnsRequest dnsRequest) {
        this.ipSelector = ipSelector;
        this.cerSelector = cerSelector;
        this.applicationProtocol = applicationProtocol;
        this.dnsRequest = dnsRequest;
    }

    public void sessionFactoryInit() {
        this.sessionFactory = new SessionFactory(this.ipSelector, this.cerSelector, this.applicationProtocol, this.dnsRequest);
    }

    @Override
    public void init() {
        System.out.println(aa);
        System.out.println();
        System.out.println();
    }

}
