package com.tincery.gaea.source.isakmp.execute;

import com.tincery.gaea.api.src.IsakmpData;
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

import java.util.List;
import java.util.Objects;


/**
 * @author gongxuanzhang
 */
@Component
@Setter
@Getter
@Slf4j
public class IsakmpReceiver extends AbstractSrcReceiver<IsakmpData> {

    @Autowired
    private PassRule passRule;

    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.ISAKMP_HEADER;
    }

    @Autowired
    public void setAnalysis(IsakmpLineAnalysis analysis) {
        this.analysis = analysis;
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
            IsakmpData isakmpData;
            try {
                isakmpData = this.analysis.pack(line);
                if (Objects.isNull(isakmpData)){
                    continue;
                }
                isakmpData.adjust();
                this.putCsvMap(isakmpData);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
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
