package com.tincery.gaea.source.email.execute;

import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
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
        // TODO: 2020/9/2  初始化有一个IP内容
        RuleRegistry ruleRegistry = RuleRegistry.getInstance();
        ruleRegistry.putRule(passRule);
        // 如果这里需要告警 才加入  ruleRegistry.putRule(alarmRule);
        CommonConfig.EmailConfig emailInfo = commonConfig.getEmail();
        // TODO: 2020/9/11 如果emailSuffixAlarm这个为true 还需要加载一个信息
    }

}
