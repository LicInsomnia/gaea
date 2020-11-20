package com.tincery.gaea.source.dns.quartz.execute;

import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

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
    private PassRule passRule;

    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    private PayloadDetector payloadDetector;

    private DnsRequest dnsRequest = new DnsRequest();

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
    protected void analysisFile(File file) {
        super.analysisFile(file);
        this.dnsRequest.output();
    }

    /****
     * 解析一行记录 填充到相应的容器中
     * @author gxz
     * @param lines 多条记录
     **/
    @Override
    protected void analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            DnsData dnsData;
            try {
                dnsData = this.analysis.pack(line);
                dnsData.adjust();
                this.dnsRequest.append(dnsData);
            } catch (Exception e) {
                log.error("错误SRC：{}", line);
                continue;
            }
            this.putCsvMap(dnsData);
        }
    }

    @Override
    public String getHead() {
        return HeadConst.DNS_HEADER;
    }

    @Override
    public void init() {
        // loadGroup();
        registryRules(passRule);
        registryRules(alarmRule);
        this.dnsRequest.initializePath();
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

}
