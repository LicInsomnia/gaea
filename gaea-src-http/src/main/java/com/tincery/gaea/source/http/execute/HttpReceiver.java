package com.tincery.gaea.source.http.execute;

import com.google.common.collect.Lists;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.gaea.core.src.SrcProperties;
import com.tincery.gaea.source.http.constant.HttpConstant;
import com.tincery.starter.base.util.NetworkUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class HttpReceiver extends AbstractSrcReceiver<HttpData> {

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

    private Map<String, List<HttpData>> outputMap = new HashMap<>();


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
        if (httpData.getDownByte() == 0) {
            category += "_down_payload_zero";
        }
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
                allHttpData = new HttpAnalysis(lines).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);
            List<Future<List<HttpData>>> tasks = new ArrayList<>();
            for (List<String> partition : partitions) {
                tasks.add(executorService.submit(new HttpAnalysis(partition)));
            }
            for (Future<List<HttpData>> task : tasks) {
                try {
                    allHttpData.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
        开始处理结果集 将结果集依次导入map
        输出
        清空list  清空map
         */
        handleOutPutMap(allHttpData);

        /*
        把整理好的数据放入csvMap
         */
        for (HttpData allHttpDatum : allHttpData) {
            putCsvMap(allHttpDatum);
        }
        allHttpData.clear();
        this.outputMap.clear();
        // list
    }
    public class HttpAnalysis implements Callable<List<HttpData>> {
        final List<String> lines;
        public HttpAnalysis(List<String> lines) {
            this.lines = lines;
        }
        @Override
        public List<HttpData> call() throws Exception {
            List<HttpData> result = new ArrayList<>();
            for (String line : lines) {
                if (StringUtils.isNotEmpty(line)) {
                    HttpData httpData;
                    try {
                        httpData = HttpReceiver.this.analysis.pack(line);
                        httpData.adjust();
                        result.add(httpData);
                    } catch (Exception e) {
                        log.error("解析实体出现了问题{}", line);
                        // TODO: 2020/9/8 实体解析有问题告警
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            return result;
        }
    }

    /**
     * 处理结果集并放入 输出map中
     * @param list 文件解析结果
     */
    private void handleOutPutMap(List<HttpData> list){
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(httpData -> {
            String subName = httpData.getSubName();
            List<HttpData> outPutMapList = outputMap.getOrDefault(subName, new ArrayList<>());
            if (CollectionUtils.isEmpty(outPutMapList)){
                //map中就没有这个key
                outPutMapList.add(httpData);
                outputMap.put(subName,outPutMapList);
            }else{
                //map中有key
                HttpData hasKey = outPutMapList.get(0);
                changeIsResponse(subName,hasKey);
                outPutMapList.set(0,hasKey);
            }
            //处理上下行数据
            if (httpData.getIsResponse()){
                srcLineSupport.setMalformedPayload(null,httpData.getPayload(),httpData);
            }else {
                srcLineSupport.setMalformedPayload(httpData.getPayload(),null,httpData);
            }
            fixHttpData(httpData);
        });

    }
    /**
     * 如果在outputMap中 修改已经在map中的httpData的属性
     * @param subName
     * @param httpData
     */
    private void changeIsResponse(String subName,HttpData httpData){
        try {
            String[] element = subName.split(StringUtils.DEFAULT_SEP, -1);
            httpData.setIsResponse("1".equals(element[24].substring(0, 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fixHttpData(HttpData httpData){
        List<HttpMeta> httpMetas = httpData.getMetas();
        // TODO 装配httpMetas
        /*for (HttpMeta httpMeta : httpMetas) {
            if (null != httpMeta.getHost()) {
                this.host.add(httpMeta.getHost());
            }
            if (null != httpMeta.urlRoot) {
                HttpToolkit.appendBuilder(this.urlRoot, httpMeta.urlRoot, ">>");
            }
            if (null != httpMeta.userAgent) {
                HttpToolkit.appendBuilder(this.userAgent, httpMeta.userAgent, ">>");
            }
            if (null != httpMeta.method) {
                HttpToolkit.appendBuilder(this.method, httpMeta.method.toString(), ">>");
            }
            if (null != httpMeta.requestContentLength) {
                HttpToolkit.appendBuilder(this.contentLength, httpMeta.requestContentLength.toString(), ">>");
            }
            if (null != httpMeta.responseContentLength) {
                HttpToolkit.appendBuilder(this.contentLength, httpMeta.responseContentLength.toString(), ">>");
            }
        }*/
    }
}
