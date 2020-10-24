package com.tincery.gaea.source.http.execute;

import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.gaea.core.src.SrcProperties;
import com.tincery.gaea.source.http.constant.HttpConstant;
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
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class HttpReceiverNew extends AbstractSrcReceiver<HttpData> {

    private final Map<String, HttpData> httpMap = new ConcurrentHashMap<>();
    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;
    @Autowired
    private IpSelector ipSelector;
    @Autowired
    private SrcLineSupport srcLineSupport;
    @Autowired
    private IpChecker ipChecker;

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
    protected void analysisFile(File file) {
        if (!file.exists()) {
            return;
        }
        List<String> lines = FileUtils.readByteArray(file).entrySet().stream().map(entry -> entry.getKey() + HttpConstant.HTTP_CONSTANT + new String(entry.getValue())).collect(Collectors.toList());
        if (lines.isEmpty()) {
            return;
        }
        List<HttpData> allHttpData = new ArrayList<>();
        int executor = this.properties.getExecutor();
        if (executor <= 1 || executor <= lines.size()) {
            /*
            在执行解析器前 先去csvMap中查看有没有这个文件。。。
             */
            try {
                analysisLine(lines);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);
            this.countDownLatch = new CountDownLatch(partitions.size());
            for (List<String> partition : partitions) {
                executorService.execute(() -> analysisLine(partition));
            }
        }

    }

    @Override
    protected void analysisLine(List<String> lines) {
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                HttpData httpData;
                try {
                    httpData = this.analysis.pack(line);
                    httpData.adjust();
                } catch (Exception e) {
                    log.error("解析实体出现了问题{}", line);
                    // TODO: 2020/9/8 实体解析有问题告警
                    e.printStackTrace();
                    continue;
                }
                String key = httpData.getKey();
                if (this.httpMap.containsKey(key)) {
                    ImpSessionData buffer = this.impSessionMap.get(pairKey);
                    buffer.merge(impSessionData);
                    this.impSessionMap.replace(pairKey, buffer);
                } else {
                    this.impSessionMap.put(key, impSessionData);
                }
            }
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
