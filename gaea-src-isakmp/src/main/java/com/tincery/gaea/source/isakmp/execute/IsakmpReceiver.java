package com.tincery.gaea.source.isakmp.execute;

import com.tincery.gaea.api.src.IsakmpData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }

    @Autowired
    public void setAnalysis(IsakmpLineAnalysis analysis) {
        this.analysis = analysis;
    }

    /**
     * @param impSessionData 单一session信息
     * @author gxz 处理单条session记录
     */
    @Override
    protected void putCsvMap(IsakmpData impSessionData) {
        if (RuleRegistry.getInstance().matchLoop(impSessionData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        String fileName = impSessionData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                impSessionData.toCsv(HeadConst.CSV_SEPARATOR),
                impSessionData.capTime);
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
            } catch (Exception e) {
                log.error("解析实体出现了问题{}", line);
                // TODO: 2020/9/8 实体解析有问题告警
                e.printStackTrace();
                continue;
            }
            this.putCsvMap(isakmpData);
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }



    @Override
    public void init() {

    }
}
