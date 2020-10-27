package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
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
 * @author gongxuanzhang
 */
@Component
@Setter
@Getter
@Slf4j
public class ImpSessionReceiver extends AbstractSrcReceiver<ImpSessionData> {

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }


    @Autowired
    public void setAnalysis(ImpSessionLineAnalysis analysis) {
        this.analysis = analysis;
    }

    private final Map<String, ImpSessionData> impSessionMap = new ConcurrentHashMap<>();

    /**
     * 多线程实现执行  基类默认实现为单线程  同analysisLine 如需要多线程实现 请重写此方法
     *
     * @author gxz
     **/
    @Override
    protected void analysisFile(File file) {
        super.analysisFile(file);
        if (this.impSessionMap.isEmpty()) {
            return;
        }
        for (ImpSessionData impSessionData : this.impSessionMap.values()) {
            putCsvMap(impSessionData);
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
            if (StringUtils.isNotEmpty(line)) {
                ImpSessionData impSessionData;
                try {
                    impSessionData = this.analysis.pack(line);
                    impSessionData.adjust();
                    String key = impSessionData.getKey();
                    String pairKey = impSessionData.getPairKey();
                    if (this.impSessionMap.containsKey(pairKey)) {
                        ImpSessionData buffer = this.impSessionMap.get(pairKey);
                        buffer.merge(impSessionData);
                        this.impSessionMap.replace(pairKey, buffer);
                    } else {
                        this.impSessionMap.put(key, impSessionData);
                    }
                } catch (Exception e) {
                    log.error("解析实体出现了问题{}", line);
                    // TODO: 2020/9/8 实体解析有问题告警
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * @param impSessionData 单一session信息
     * @author gxz 处理单条session记录
     */
    @Override
    protected void putCsvMap(ImpSessionData impSessionData) {
        if (RuleRegistry.getInstance().matchLoop(impSessionData)) {
            // 过滤过滤
            return;
        }
        String category = "session";
        if (impSessionData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
        String fileName = impSessionData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                impSessionData.toCsv(HeadConst.CSV_SEPARATOR),
                impSessionData.capTime);
    }

    @Override
    protected String getDataWarehouseCsvPath() {
        return NodeInfo.getDataWarehouseCsvPathByCategory("session");
    }

    @Override
    public void init() {

    }
}
