package com.tincery.gaea.source.pptpandl2tp.execute;

import com.tincery.gaea.api.src.Pptpandl2tpData;
import com.tincery.gaea.api.src.SshData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
import org.springframework.stereotype.Service;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class Pptpandl2tpReceiver extends AbstractSrcReceiver<Pptpandl2tpData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;


    @Autowired
    public void setAnalysis(Pptpandl2tpLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.PPTPANDL2TP_HEADER;
    }

    @Override
    protected void putCsvMap(Pptpandl2tpData pptpandl2tpData) {
        if (RuleRegistry.getInstance().matchLoop(pptpandl2tpData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        if (pptpandl2tpData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = pptpandl2tpData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                pptpandl2tpData.toCsv(HeadConst.CSV_SEPARATOR),
                pptpandl2tpData.capTime);
    }

    @Override
    public void init() {
        registryRules(passrule);
        registryRules(alarmRule);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }


}
