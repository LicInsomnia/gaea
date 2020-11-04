package com.tincery.gaea.ods.httpanalysis.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.base.MatchHttpConfig;
import com.tincery.gaea.api.base.TargetAttribute;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
@Slf4j
public class HttpAnalysisReceiver implements Receiver {

    private Map<String, MatchHttpConfig> matchMap;

    private List<JSONObject> noHostList = new CopyOnWriteArrayList<>();
    private List<JSONObject> noMatchStrList = new CopyOnWriteArrayList<>();
    private List<JSONObject> noHitList = new CopyOnWriteArrayList<>();
    private List<JSONObject> successList = new CopyOnWriteArrayList<>();

    private String httpAnalysisNoHostPath;
    private String httpAnalysisNoMatchStrPath;
    private String httpAnalysisNoHitPath;


    private static final int executorCount = 4;

    private static ThreadPoolExecutor executorService;

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
        List<String> lines = FileUtils.readLine(file);
        if (lines.size() <= executorCount) {
            for (String line : lines) {
                analysisHttp(line);
            }
        } else {
            List<List<String>> partition = Lists.partition(lines, (lines.size() / executorCount) + 1);
            CountDownLatch countDownLatch = new CountDownLatch(executorCount);
            for (List<String> list : partition) {
                executorService.execute(new HttpAnalysis(list, countDownLatch));
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            free();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("写入文件发生错误");
        }
    }

    private void analysisHttp(String httpLine) {
        JSONObject httpJson = JSON.parseObject(httpLine);
        List<MatchHttpConfig.Extract> extracts = selectExtract(httpJson);
        // 未命中host
        if (extracts == null) {
            noHostList.add(httpJson);
            return;
        }
        List<List<MatchHttpConfig.Match>> matches = selectMatch(extracts, httpJson);
        // 未命中matchStr
        if (matches == null) {
            noMatchStrList.add(httpJson);
            return;
        }
        // 抽取失败
        List<JSONObject> hitDatas = hitData(matches, httpJson);
        if (CollectionUtils.isEmpty(hitDatas)) {
            noHitList.add(httpJson);
            return;
        }
        successList.add(httpJson);
    }

    private void free() throws IOException {
        String fileFile = "/"+ Instant.now().toEpochMilli()+".json";
        writeFile(this.noHostList,this.httpAnalysisNoHostPath+ fileFile);
        writeFile(this.noMatchStrList,this.httpAnalysisNoMatchStrPath+ fileFile);
        writeFile(this.noHitList,this.httpAnalysisNoHitPath+ fileFile);
        List<TargetAttribute> successData =
                this.successList.stream().map(jsonObject -> jsonObject.toJavaObject(TargetAttribute.class)).collect(Collectors.toList());
        System.out.println(1);
    }

    private void writeFile(List<JSONObject> jsons, String path) throws IOException {
        if(CollectionUtils.isEmpty(jsons)){
            return;
        }
        try (FileWriter noHitFileWriter = new FileWriter(new File(path))) {
            for (JSONObject jsonObject : jsons) {
                noHitFileWriter.write(jsonObject.toString() + "\n");
                noHitFileWriter.flush();
            }
        }
    }


    private List<MatchHttpConfig.Extract> selectExtract(JSONObject httpJson) {
        if(!httpJson.containsKey("host")){
            return null;
        }
        String host = httpJson.getString("host");
        MatchHttpConfig matchHttpConfig = matchMap.get(host);
        if (matchHttpConfig == null) {
            return null;
        }
        return matchHttpConfig.getExtract();
    }

    private List<List<MatchHttpConfig.Match>> selectMatch(List<MatchHttpConfig.Extract> extracts, JSONObject httpJson) {
        String url = httpJson.getString("url");
        if(url == null){
            return null;
        }
        for (MatchHttpConfig.Extract extract : extracts) {
            if (url.startsWith(extract.getMatchStr())) {
                return extract.getItems();
            }
        }
        return null;
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
        FileUtils.checkPath(httpAnalysisNoHostPath, httpAnalysisNoMatchStrPath, httpAnalysisNoHitPath);

    }

    public class HttpAnalysis implements Runnable {

        final private List<String> lines;
        final CountDownLatch countDownLatch;

        public HttpAnalysis(List<String> lines, CountDownLatch countDownLatch) {
            this.lines = lines;
            this.countDownLatch = countDownLatch;
        }


        @Override
        public void run() {
            for (String line : this.lines) {
                analysisHttp(line);
            }
            countDownLatch.countDown();
        }
    }
}
