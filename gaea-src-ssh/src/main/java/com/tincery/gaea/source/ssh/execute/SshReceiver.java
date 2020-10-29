package com.tincery.gaea.source.ssh.execute;

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
public class SshReceiver extends AbstractSrcReceiver<SshData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;


    @Autowired
    public void setAnalysis(SshLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SSH_HEADER;
    }

    /**
     * @param sshData 单一session信息
     * @author gxz 处理单条session记录
     */
    @Override
    protected void putCsvMap(SshData sshData) {
        if (RuleRegistry.getInstance().matchLoop(sshData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        if (sshData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = sshData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                sshData.toCsv(HeadConst.CSV_SEPARATOR),
                sshData.capTime);
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
