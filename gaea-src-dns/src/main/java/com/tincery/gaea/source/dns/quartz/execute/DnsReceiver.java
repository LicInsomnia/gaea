package com.tincery.gaea.source.dns.quartz.execute;

import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author gxz
 */
@Component
@Slf4j
@Setter
@Getter
@Service
public class DnsReceiver extends AbstractSrcReceiver<DnsData> {


    @Autowired
    private ImpTargetSetupDao impTargetSetupDao;

    @Autowired
    private PassRule passrule;

    @Autowired
    private PayloadDetector payloadDetector;


    @Autowired
    public void setAnalysis(DnsLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.DNS_HEADER;
    }

    @Override
    public void init() {
        // loadGroup();
        registryRules(passrule);
    }

    public void registryRules(PassRule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }


}
