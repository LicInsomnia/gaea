package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author gongxuanzhang
 */
@Component
@Slf4j
public class EspAndAhReceiver extends AbstractSrcReceiver<EspAndAhData> {

    private final List<EspAndAhData> espAndAhDataList = new CopyOnWriteArrayList<>();

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
        return HeadConst.ESPANDAH_HEADER;
    }

    @Autowired
    public void setAnalysis(EspAndAhLineAnalysis analysis) {
        this.analysis = analysis;
    }

    /**
     * 多线程实现执行  基类默认实现为单线程  同analysisLine 如需要多线程实现 请重写此方法
     *
     * @author gxz
     **/
    @Override
    protected void analysisFile(File file) {
        super.analysisFile(file);
        if (this.espAndAhDataList.isEmpty()) {
            return;
        }
        for (EspAndAhData data : this.espAndAhDataList) {
            data.adjust();
            putCsvMap(data);
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
                EspAndAhData espAndAhData;
                try {
                    espAndAhData = this.analysis.pack(line);
                    merge(espAndAhData);
                } catch (Exception e) {
                    log.error("解析实体出现了问题{}", line);
                    // TODO: 2020/9/8 实体解析有问题告警
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void merge(EspAndAhData espAndAhData) {
        for (EspAndAhData data : this.espAndAhDataList) {
            if (espAndAhData.getKey().equals(data.getKey())) {
                if (espAndAhData.getC2sSpi().equals(data.getC2sSpi()) ||
                        espAndAhData.getS2cSpi().equals(data.getS2cSpi()) ||
                        ("".equals(espAndAhData.getC2sSpi()) && !"".equals(data.getC2sSpi())) ||
                        ("".equals(espAndAhData.getS2cSpi()) && !"".equals(data.getS2cSpi()))) {
                    data.merge(espAndAhData);
                    return;
                } else {
                    this.espAndAhDataList.add(espAndAhData);
                    return;
                }
            }
        }
        this.espAndAhDataList.add(espAndAhData);
    }

    @Override
    public void init() {
        // loadGroup();
        registryRules(passRule);
        registryRules(alarmRule);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

}
