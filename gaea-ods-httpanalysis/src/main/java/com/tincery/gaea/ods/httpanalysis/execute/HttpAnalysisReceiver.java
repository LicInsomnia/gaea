package com.tincery.gaea.ods.httpanalysis.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.base.MatchHttpConfig;
import com.tincery.gaea.api.base.TargetAttribute;
import com.tincery.gaea.api.ods.HttpAnalysisGroup;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.MatchHttpDao;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
@Slf4j
public class HttpAnalysisReceiver implements Receiver {

    private Map<String, MatchHttpConfig> matchMap;

    private static final ThreadPoolExecutor executorService;

    private String httpAnalysisNoHostPath;
    private String httpAnalysisNoMatchStrPath;
    private String httpAnalysisNoHitPath;
    private String httpAnalysisTrashPath;
    private String httpAnalysisSuccessPath;

    private static final int executorCount = 4;

    static {
        executorService = new ThreadPoolExecutor(
                CPU + 1,
                CPU * 2,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(4096),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Autowired
    private MatchHttpDao matchHttpDao;


    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
        log.info("开始解析文件{}", file.getPath());
        List<String> lines = FileUtils.readLine(file);
        List<List<String>> partition = Lists.partition(lines, (lines.size() / executorCount) + 1);
        List<Future<HttpAnalysisGroup>> futures = new ArrayList<>();
        for (List<String> list : partition) {
            futures.add(executorService.submit(new HttpAnalysis(list)));
        }
        try {
            HttpAnalysisGroup finish = HttpAnalysisGroup.init();
            for (Future<HttpAnalysisGroup> future : futures) {
                finish.merge(future.get());
            }
            free(file, finish);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析文件{}出现错误", file.getPath());
        }
    }


    private void free(File file, HttpAnalysisGroup httpAnalysisGroup) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        int dayOfMonth = now.getDayOfMonth();
        String dirName = "/" + year + "-" + monthValue + "-" + dayOfMonth;
        String httpAnalysisNoHitPath = this.httpAnalysisNoHitPath + dirName;
        String httpAnalysisNoHostPath = this.httpAnalysisNoHostPath + dirName;
        String httpAnalysisNoMathStrPath = this.httpAnalysisNoMatchStrPath + dirName;
        String httpAnalysisTrashPath = this.httpAnalysisTrashPath + dirName;
        FileUtils.checkPath(httpAnalysisNoHitPath, httpAnalysisNoHostPath, httpAnalysisNoMathStrPath,
                httpAnalysisTrashPath);
        String fileFile = "/" + Instant.now().toEpochMilli() + ".json";
        List<TargetAttribute> successData =
                httpAnalysisGroup.getSuccessList().stream().map(jsonObject -> jsonObject.toJavaObject(TargetAttribute.class)).collect(Collectors.toList());
        List<JSONObject> successJson =
                successData.stream().map(JSON::toJSONString).map(jsonString -> (JSONObject) JSONObject.parse(jsonString)).collect(Collectors.toList());
        log.info("输出未匹配Host记录：{}条,未匹配URL记录：{}条,未命中规则记录：{}条，垃圾{}条,入库：{}条", httpAnalysisGroup.getNoHostList().size(),
                httpAnalysisGroup.getNoMatchStrList().size(), httpAnalysisGroup.getNoHitList().size(),
                httpAnalysisGroup.getTrashList().size(), successData.size());
        writeFile(httpAnalysisGroup.getNoHostList(), httpAnalysisNoHostPath + fileFile);
        writeFile(httpAnalysisGroup.getNoMatchStrList(), httpAnalysisNoMathStrPath + fileFile);
        writeFile(httpAnalysisGroup.getNoHitList(), httpAnalysisNoHitPath + fileFile);
        writeFile(httpAnalysisGroup.getTrashList(), httpAnalysisTrashPath + fileFile);
        writeFile(successJson, httpAnalysisSuccessPath + fileFile);
        boolean delete = file.delete();
        log.info("删除{}{}", file.getPath(), delete ? "成功" : "失败");

    }

    private void writeFile(List<JSONObject> jsons, String path) throws IOException {
        if (CollectionUtils.isEmpty(jsons)) {
            return;
        }
        try (FileWriter noHitFileWriter = new FileWriter(new File(path))) {
            for (JSONObject jsonObject : jsons) {
                noHitFileWriter.write(jsonObject.toString() + "\n");
                noHitFileWriter.flush();
            }
        }
    }


    private List<JSONObject> hitData(List<List<MatchHttpConfig.Match>> matches, JSONObject httpJson) {
        List<JSONObject> hitedJson = new ArrayList<>();
        for (List<MatchHttpConfig.Match> matchGroup : matches) {
            JSONObject jsonObject = MatchHttpConfig.groupHitAndFillInfo(matchGroup, httpJson);
            if (jsonObject != null) {
                hitedJson.add(jsonObject);
            }
        }
        return hitedJson;
    }


    @Override
    public void init() {
        matchMap = matchHttpDao.findAll().stream().collect(Collectors.toConcurrentMap(MatchHttpConfig::getId,
                Function.identity()));
        String jsonElements = NodeInfo.getDataWarehouseJsonPathByCategory("httpelements");
        httpAnalysisNoHostPath = jsonElements + "/noHost";
        httpAnalysisNoMatchStrPath = jsonElements + "/noMatchStr";
        httpAnalysisNoHitPath = jsonElements + "/noHit";
        httpAnalysisNoHitPath = jsonElements + "/noHit";
        httpAnalysisTrashPath = jsonElements + "/trash";
        httpAnalysisSuccessPath = NodeInfo.getCacheByCategory("targetAttribute");
        FileUtils.checkPath(httpAnalysisNoHostPath, httpAnalysisNoMatchStrPath, httpAnalysisNoHitPath,
                httpAnalysisTrashPath,httpAnalysisSuccessPath);

    }

    public class HttpAnalysis implements Callable<HttpAnalysisGroup> {

        final private List<String> lines;

        private final HttpAnalysisGroup httpAnalysisGroup;

        public HttpAnalysis(List<String> lines) {
            this.lines = lines;
            this.httpAnalysisGroup = HttpAnalysisGroup.init();
        }

        @Override
        public HttpAnalysisGroup call() throws Exception {
            for (String httpLine : this.lines) {
                JSONObject httpJson = JSON.parseObject(httpLine);
                List<MatchHttpConfig.Extract> extracts = selectExtract(httpJson);
                // 未命中host
                if (extracts == null) {
                    continue;
                }
                List<List<MatchHttpConfig.Match>> matches = selectMatch(extracts, httpJson);
                // 未命中matchStr
                if (matches == null) {
                    continue;
                }
                // 抽取失败
                List<JSONObject> hitDatas = hitData(matches, httpJson);
                if (CollectionUtils.isEmpty(hitDatas)) {
                    httpAnalysisGroup.getNoHitList().add(httpJson);
                    continue;
                }
                httpAnalysisGroup.getSuccessList().addAll(hitDatas);
            }
            return httpAnalysisGroup;
        }

        private List<MatchHttpConfig.Extract> selectExtract(JSONObject httpJson) {
            String host = httpJson.getOrDefault("host", "").toString();
            MatchHttpConfig matchHttpConfig = matchMap.get(host);
            if (matchHttpConfig == null) {
                httpAnalysisGroup.getNoHostList().add(httpJson);
                return null;
            }
            return matchHttpConfig.getExtract();
        }


        private List<List<MatchHttpConfig.Match>> selectMatch(List<MatchHttpConfig.Extract> extracts,
                                                              JSONObject httpJson) {
            String content = httpJson.getString("content");
            if (content == null) {
                httpAnalysisGroup.getNoMatchStrList().add(httpJson);
                return null;
            }
            for (MatchHttpConfig.Extract extract : extracts) {
                if (content.startsWith(extract.getMatchStr())) {
                    if (extract.isTrash()) {
                        httpAnalysisGroup.getTrashList().add(httpJson);
                    }
                    return extract.getItems();
                }
            }
            httpAnalysisGroup.getNoMatchStrList().add(httpJson);
            return null;
        }
    }
}
