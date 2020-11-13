package com.tincery.gaea.source.alarm.execute;

import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.src.AlarmTupleData;
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


/**
 * @author gongxuanzhang
 */
@Component
@Setter
@Getter
@Slf4j
public class AlarmReceiver extends AbstractSrcReceiver<AlarmTupleData> {

    @Autowired
    private PassRule passRule;

    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    //TODO 换HEADER
    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }

    @Autowired
    public void setAnalysis(AlarmLineAnalysis analysis) {
        this.analysis = analysis;
    }


    /**
     * 解析文件 并在输出之前进行文件整理
     *
     * @param file 一个输入dat文件
     */
    @Override
    protected void analysisFile(File file) {
        super.analysisFile(file);
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
            AlarmTupleData alarmTupleData;
            try {
                alarmTupleData = this.analysis.pack(line);
                alarmTupleData.adjust();
                AlarmMaterialData alarmMaterialData = new AlarmMaterialData(alarmTupleData);

            } catch (Exception e) {
                log.error("错误SRC：{}", line);
            }
        }
    }


    @Override
    public void init() {

    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

}
