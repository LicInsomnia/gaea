package com.tincery.gaea.source.email.execute;

import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gxz
 */

@Component
@Slf4j
@Setter
@Getter
public class EmailReceiver extends AbstractSrcReceiver<EmailData> {


    private boolean emailSuffixAlarm;

    @Autowired
    private AlarmRule alarmRule;
    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private PassRule passRule;
    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Autowired
    public void setAnalysis(EmailLineAnalysis analysis) {
        this.analysis = analysis;
    }


    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.EMAIL_HEADER;
    }

    @Override
    public void init() {
        // loadGroup();
        registryRules(passRule);
        registryRules(alarmRule);
        CommonConfig.EmailConfig emailInfo = commonConfig.getEmail();
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

}
