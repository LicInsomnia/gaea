package com.tincery.gaea.source.http.execute;

import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
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
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class HttpReceiver extends AbstractSrcReceiver<HttpData> {

    private final Map<String, HttpData> httpMap = new ConcurrentHashMap<>();

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    public void setAnalysis(HttpLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.HTTP_HEADER;
    }

    /**
     * @param httpData 单一httpData信息
     * @author gxz 处理单条httpData记录
     */
    @Override
    protected void putCsvMap(HttpData httpData) {
        if (RuleRegistry.getInstance().matchLoop(httpData)) {
            // 过滤过滤
            return;
        }
        String category = ApplicationInfo.getCategory();
        String fileName = httpData.getDateSetFileName(category);
        this.appendCsvData(fileName,
                httpData.toCsv(HeadConst.CSV_SEPARATOR),
                httpData.capTime);
    }

    @Override
    public void init() {
        registryRules(passrule);
        registryRules(alarmRule);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

    @Override
    protected List<String> getLines(File file) {
        // TODO 返回dat转行解析器
        return new ArrayList<>();
    }

    @Override
    protected void analysisFile(File file) {
        super.analysisFile(file);
        for (HttpData httpData : this.httpMap.values()) {
            httpData.adjust();
            putCsvMap(httpData);
            putCacheJson(httpData);
            putDataWarehouse(httpData);
        }
    }

    private void putCacheJson(HttpData httpData) {
    }

    private void putDataWarehouse(HttpData httpData) {
    }

    @Override
    protected void analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                HttpData httpData;
                try {
                    httpData = this.analysis.pack(line);
                } catch (Exception e) {
                    log.error("解析实体出现了问题{}", line);
                    // TODO: 2020/9/8 实体解析有问题告警
                    e.printStackTrace();
                    continue;
                }
                String key = httpData.getKey();
                if (this.httpMap.containsKey(key)) {
                    HttpData buffer = this.httpMap.get(key);
                    buffer.merge(httpData);
                } else {
                    this.httpMap.put(key, httpData);
                }
            }
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
