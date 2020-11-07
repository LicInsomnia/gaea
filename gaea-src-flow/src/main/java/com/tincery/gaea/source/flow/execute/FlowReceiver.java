package com.tincery.gaea.source.flow.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.FlowData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
@Service
public class FlowReceiver extends AbstractSrcReceiver<FlowData> {

    private Map<String, FlowData> flowDataMap = new ConcurrentHashMap<>();
    private Map<String, FlowData> impFlowDataMap = new ConcurrentHashMap<>();

    @Autowired
    public void setAnalysis(FlowLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return null;
    }

    @Override
    protected void analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                FlowData flowData;
                try {
                    flowData = this.analysis.pack(line);
                    if (null == flowData) {
                        continue;
                    }
                    String key = flowData.getFlowKey();
                    if (flowData.getFlowStatistic().getImp()) {
                        if (this.impFlowDataMap.containsKey(key)) {
                            FlowData buffer = this.impFlowDataMap.get(key);
                            buffer.merge(flowData);
                        } else {
                            this.impFlowDataMap.put(key, flowData);
                        }
                    } else {
                        if (this.flowDataMap.containsKey(key)) {
                            FlowData buffer = this.flowDataMap.get(key);
                            buffer.merge(flowData);
                        } else {
                            this.flowDataMap.put(key, flowData);
                        }
                    }
                } catch (Exception e) {
                    this.errorFileWriter.write(line);
                }
            }
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    @Override
    protected void free() {
        String file = ApplicationInfo.getCacheByCategory() + "/" + ApplicationInfo.getCategory() + '_' + System.currentTimeMillis() + ".json";
        FileWriter fileWriter = new FileWriter(file);
        for (FlowData flowData : this.flowDataMap.values()) {
            flowData.adjust();
            putJson(flowData, fileWriter);
        }
        for (FlowData flowData : this.impFlowDataMap.values()) {
            flowData.adjust();
            putJson(flowData, fileWriter);
        }
        fileWriter.close();
    }

    private void putJson(FlowData flowData, FileWriter fileWriter) {
        fileWriter.write(JSONObject.toJSONString(flowData));
    }

    @Override
    public void init() {
        // loadGroup();
    }

}
