package com.tincery.gaea.source.wechat.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.WeChatData;
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
public class WeChatReceiver extends AbstractSrcReceiver<WeChatData> {

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;

    private final CopyOnWriteArrayList<WeChatData> weChatList = new CopyOnWriteArrayList<>();


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

    @Override
    protected void free() {

        HashSet<WeChatData> weChatData = new HashSet<>(weChatList);
        for (WeChatData weChat : weChatData) {
            this.putCsvMap(weChat);
        }
        super.free();
        outPutJson(weChatData);
        this.weChatList.clear();
    }

    private void outPutJson(Set<WeChatData> weChatDataSet){
        String dataWarehouseJsonFile = ApplicationInfo.getDataWarehouseJsonPathByCategory() + "/" + ApplicationInfo.getCategory() + "_" +
                System.currentTimeMillis() + ".json";
        FileWriter dataWarehouseJsonFileWriter = new FileWriter(dataWarehouseJsonFile);
        for (WeChatData weChatData : weChatDataSet) {
            putJson(weChatData.toJsonObjects(), dataWarehouseJsonFileWriter);
        }
        dataWarehouseJsonFileWriter.close();
        weChatDataSet.clear();
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
            WeChatData weChatData;
            try {
                weChatData = this.analysis.pack(line);
                weChatData.adjust();
                this.weChatList.add(weChatData);
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
