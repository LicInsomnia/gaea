package com.tincery.gaea.source.http.execute;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import com.tincery.gaea.source.http.constant.HttpConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<String, HttpData> httpMap = new ConcurrentHashMap<>();

    @Autowired
    private PassRule passrule;
    @Autowired
    private AlarmRule alarmRule;

    @Autowired
    private HttpLineSupport httpLineSupport;

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
        long start = System.currentTimeMillis();
        List<String> collect = FileUtils.readByteArray(file).entrySet().stream().map(entry -> entry.getKey() +
                HttpConstant.HTTP_CONSTANT + entry.getValue().getKey() +
                HttpConstant.HTTP_CONSTANT + new String(entry.getValue().getValue(), StandardCharsets.ISO_8859_1) +
                HttpConstant.HTTP_CONSTANT + entry.getValue().getValue().length).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        System.out.println(end-start + "读取文件" );
        return collect;
    }

    /**
     * 解析文件 并在输出之前进行文件整理
     *
     * @param file 一个输入dat文件
     */
    @Override
    protected void analysisFile(File file) {
        if (!file.exists()) {
            log.info("文件：{}已被处理" + file.getAbsolutePath());
            return;
        }
        List<String> lines = getLines(file);
        if (lines.isEmpty()) {
            return;
        }
        int executor = this.properties.getExecutor();
        long ll = Instant.now().toEpochMilli();
        List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);
        List<Future<Map<String, HttpData>>> futureTasks = new ArrayList<>();
        for (List<String> partition : partitions) {
            futureTasks.add(executorService.submit(new HttpAnalysisLineProducer(partition)));
        }
        for (Future<Map<String, HttpData>> futureTask : futureTasks) {
            try {
                futureTask.get().forEach((key, httpData) -> {
                    this.httpMap.merge(key, httpData, HttpData::merge);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("下面是处理数据时间"+(ll-Instant.now().toEpochMilli()));
        FileWriter cacheJsonFileWriter = getCacheFileWriter();
        FileWriter dataWarehouseJsonFileWriter = getDataWareHorseFileWriter();
        System.out.println("一共有多少个"+this.httpMap.size());
        long l = Instant.now().toEpochMilli();
        for (HttpData httpData : this.httpMap.values()) {
            httpData.adjust();
            // 装载Location
            fixHttpDataLocation(httpData);
            // 输出CSV
            /*
            csv  每个meta单独输出
             */
            putCsvMap(httpData);
            // 输出cache中JSON，供数据入库
            /*
            json  要把meta取出来集合toJson
            要遍历metas 取所有的common
            cache的要把content截取  超过4096的不要
             */
            putJson(httpData.toJsonObjects(), cacheJsonFileWriter);
            // 输出输出仓库的JSON，供后面的http分析使用
            putJson(httpData.toJsonObjects(), dataWarehouseJsonFileWriter);
        }
        System.out.println("循环用了"+(l-Instant.now().toEpochMilli()));
        cacheJsonFileWriter.close();
        dataWarehouseJsonFileWriter.close();
    }

    private void putJson(List<JSONObject> jsonObjects, FileWriter fileWriter) {
        for (JSONObject jsonObject : jsonObjects) {
            fileWriter.write(JSONObject.toJSONString(jsonObject));
        }
    }

    /**
     * 加锁获得fileWriter
     */
    private synchronized FileWriter getCacheFileWriter() {
        String cacheJsonFile = ApplicationInfo.getCachePathByCategory() + "/" + ApplicationInfo.getCategory() + "_" +
                System.currentTimeMillis() + ".json";
        return new FileWriter(cacheJsonFile);
    }

    /**
     * 加锁获得fileWriter
     */
    private synchronized FileWriter getDataWareHorseFileWriter() {
        String dataWarehouseJsonFile =
                ApplicationInfo.getDataWarehouseJsonPathByCategory() + "/" + ApplicationInfo.getCategory() + "_" +
                        System.currentTimeMillis() + ".json";
        return new FileWriter(dataWarehouseJsonFile);
    }

    /**
     * 装载httpData的两个Location数据
     *
     * @param httpData 一条数据
     */
    private void fixHttpDataLocation(HttpData httpData) {
        httpData.setServerLocation(this.httpLineSupport.getLocation(httpData.getServerIp()));
        httpData.setClientLocation(this.httpLineSupport.getLocation(httpData.getClientIp()));
    }

    public class HttpAnalysisLineProducer implements Callable<Map<String, HttpData>> {

        private final List<String> lines;

        public HttpAnalysisLineProducer(List<String> lines) {
            this.lines = lines;
        }


        @Override
        public Map<String, HttpData> call() throws Exception {
            Map<String, HttpData> result = new HashMap<>(this.lines.size());
            for (String line : lines) {
                if (StringUtils.isNotEmpty(line)) {
                    HttpData httpData;
                    try {
                        httpData = HttpReceiver.this.analysis.pack(line);
                        String key = httpData.getKey();
                        if (result.containsKey(key)) {
                            HttpData buffer = result.get(key);
                            buffer.merge(httpData);
                        } else {
                            result.put(key, httpData);
                        }
                    } catch (Exception e) {
                        log.error("错误SRC:{}", line);
                    }
                }
            }
            return result;
        }
    }
}
