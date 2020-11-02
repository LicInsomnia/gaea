package com.tincery.gaea.source.wechat.execute;

import com.tincery.gaea.api.src.WeChatData;
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
public class WeChatReceiver extends AbstractSrcReceiver<WeChatData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;


    @Autowired
    public void setAnalysis(WeChatLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.WECHAT_HEADER;
    }

    /**
     * @param weChatData 单一session信息
     * @author gxz 处理单条session记录
     */
    @Override
    protected void putCsvMap(WeChatData weChatData) {
        if (RuleRegistry.getInstance().matchLoop(weChatData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        if (weChatData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = weChatData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                weChatData.toCsv(HeadConst.CSV_SEPARATOR),
                weChatData.capTime);
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
