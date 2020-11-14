package com.tincery.gaea.source.alarm.execute;

import com.alibaba.fastjson.JSON;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.src.AlarmTupleData;
import com.tincery.gaea.api.src.extension.AlarmExtension;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private Map<String, AlarmMaterialData> alarmMap = new HashMap<>();

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
                if (alarmTupleData.isEmpty()) {
                    continue;
                }
                AlarmMaterialData alarmMaterialData = new AlarmMaterialData(alarmTupleData);
                List<AlarmExtension> alarmExtensions = alarmTupleData.getAlarmExtension();
                for (AlarmExtension alarmExtension : alarmExtensions) {
                    AlarmMaterialData materialData = new AlarmMaterialData();
                    BeanUtils.copyProperties(alarmMaterialData, materialData);
                    materialData.appendExtension(alarmExtension);
                    materialData.setKey();
                    if (this.alarmMap.containsKey(materialData.getKey())) {
                        AlarmMaterialData buffer = this.alarmMap.get(materialData.getKey());
                        buffer.merge(materialData);
                    } else {
                        this.alarmMap.put(materialData.getKey(), materialData);
                    }
                }
            } catch (Exception e) {
                log.error("错误SRC：{}", line);
            }
        }
        output();
    }


    @Override
    public void init() {

    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

    private void output() {
        FileWriter fileWriter = new FileWriter(NodeInfo.getAlarmMaterial() + "/defaultAlarm_" + System.currentTimeMillis() + ".json");
        for (AlarmMaterialData alarmMaterialData : this.alarmMap.values()) {
            fileWriter.write(JSON.toJSONString(alarmMaterialData));
        }
        log.info("成功输出{}条告警信息", this.alarmMap.size());
        fileWriter.close();
    }

}
