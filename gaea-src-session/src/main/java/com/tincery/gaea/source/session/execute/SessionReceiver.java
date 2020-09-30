package com.tincery.gaea.source.session.execute;

import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
public class SessionReceiver extends AbstractSrcReceiver<SessionData> {

    @Autowired
    private PassRule passrule;

    @Autowired
    public void setAnalysis(SessionLineAnalysis analysis) {
        this.analysis = analysis;
    }


    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }

    /**
     * @param sessionData 单一session信息
     * @author gxz 处理单条session记录
     */
    @Override
    protected void putCsvMap(SessionData sessionData) {
        if (RuleRegistry.getInstance().matchLoop(sessionData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        if (sessionData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = sessionData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                sessionData.toCsv(HeadConst.CSV_SEPARATOR),
                sessionData.capTime);
    }

    @Override
    public void init() {
        registryRules(passrule);
    }

    public void registryRules(PassRule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }



}
