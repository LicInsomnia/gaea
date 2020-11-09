package com.tincery.gaea.source.openven.execute;

import com.tincery.gaea.api.src.OpenVpnData;
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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gxz
 */
@Component
@Slf4j
@Setter
@Getter
public class OpenVpnReceiver extends AbstractSrcReceiver<OpenVpnData> {

    private final Map<String, OpenVpnData> openVpnMap = new ConcurrentHashMap<>();
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
    protected void analysisFile(File file) {
        super.analysisFile(file);
        if (this.openVpnMap.isEmpty()) {
            return;
        }
        for (OpenVpnData openVpnData : this.openVpnMap.values()) {
            openVpnData.adjust();
            putCsvMap(openVpnData);
        }
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
            OpenVpnData openVpnData;
            try {
                openVpnData = this.analysis.pack(line);
                String key = openVpnData.getKey();
                if (openVpnData.getDataType() <= -1) {
                    putCsvMap(openVpnData);
                    continue;
                }
                if (this.openVpnMap.containsKey(key)) {
                    OpenVpnData buffer = this.openVpnMap.get(key);
                    buffer.merge(openVpnData);
                } else {
                    this.openVpnMap.put(key, openVpnData);
                }
            } catch (Exception e) {
                log.error("错误SRC：{}", line);
            }
        }
    }

    @Override
    public void init() {
        registryRules(passRule);
        registryRules(alarmRule);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }


}
