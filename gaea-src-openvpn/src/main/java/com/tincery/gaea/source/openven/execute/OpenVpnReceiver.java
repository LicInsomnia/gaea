package com.tincery.gaea.source.openven.execute;

import com.tincery.gaea.api.src.OpenVpnData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
import org.springframework.stereotype.Component;

/**
 * @author gxz
 */
@Component
@Slf4j
@Setter
@Getter
public class OpenVpnReceiver extends AbstractSrcReceiver<OpenVpnData> {

    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    private PassRule passRule;


    @Autowired
    public void setAnalysis(OpenVpnLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.OPENVPN_HEADER;
    }


    @Override
    protected void putCsvMap(OpenVpnData openVpnData) {
        if (RuleRegistry.getInstance().matchLoop(openVpnData)) {
            // 过滤规则  其中alarm规则是有同步块的
            return;
        }
        appendCsvData(openVpnData.getDateSetFileName(ApplicationInfo.getCategory()),
                openVpnData.toCsv(HeadConst.CSV_SEPARATOR),
                openVpnData.getCapTime()
        );
    }

    @Override
    public void init() {
        // TODO: 2020/9/2  初始化有一个IP内容
        RuleRegistry.getInstance().putRule(alarmRule).putRule(passRule);
    }


}
