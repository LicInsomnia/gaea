package com.tincery.gaea.source.bitcoin.config.property.execute;

import com.tincery.gaea.api.src.BitCoinData;
import com.tincery.gaea.api.src.QQData;
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

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class BitcoinReceiver extends AbstractSrcReceiver<BitCoinData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;

    private final CopyOnWriteArrayList<BitCoinData> bitCoinList = new CopyOnWriteArrayList<>();


    @Autowired
    public void setAnalysis(BitCoinAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.BITCOIN;
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
