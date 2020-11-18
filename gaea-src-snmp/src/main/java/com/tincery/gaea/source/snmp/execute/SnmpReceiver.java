package com.tincery.gaea.source.snmp.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.QQData;
import com.tincery.gaea.api.src.SnmpData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class SnmpReceiver extends AbstractSrcReceiver<SnmpData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;

    private final CopyOnWriteArrayList<SnmpData> qqList = new CopyOnWriteArrayList<>();


    @Autowired
    public void setAnalysis(SnmpAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SNMP_HEADER;
    }

    @Override
    protected void free() {
        super.free();
    }

    private void outPutJson(Set<QQData> qqDataSet){
        String dataWarehouseJsonFile = ApplicationInfo.getDataWarehouseJsonPathByCategory() + "/" + ApplicationInfo.getCategory() + "_" +
                System.currentTimeMillis() + ".json";
        FileWriter dataWarehouseJsonFileWriter = new FileWriter(dataWarehouseJsonFile);
        for (QQData qqData : qqDataSet) {
            putJson(qqData.toJsonObjects(), dataWarehouseJsonFileWriter);
        }
        dataWarehouseJsonFileWriter.close();
        qqDataSet.clear();
    }

    private void putJson(JSONObject jsonObjects, FileWriter fileWriter) {
            fileWriter.write(JSONObject.toJSONString(jsonObjects));
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
            SnmpData snmpData;
            try {
                snmpData = this.analysis.pack(line);
                snmpData.adjust();
                this.qqList.add(snmpData);
            } catch (Exception e) {
                log.error("错误SRC：{}", line);
            }

        }
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
