package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.api.dw.ReorganizationDataWareGroup;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.dw.AbstractDataWarehouseReceiver;
import com.tincery.gaea.core.dw.DwProperties;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Component
@Slf4j
public class ReorganizationReceiver extends AbstractDataWarehouseReceiver {

    private static final String[] sesssionCategorys = {
            "session",
            "ssl",
            "openvpn",
            "dns",
            "http",
            "email",
            "isakmp",
            "ssh",
            "ftpandtelnet",
            "espandah"
    };
    private static int impSessionCount = 0;
    private static int assetCount = 0;
    @Autowired
    private ImpSessionCsvFilter impSessionCsvFilter;
    @Autowired
    private AssetCsvFilter assetCsvFilter;
    @Autowired
    private ReorganizationFactory reorganizationFactory;

    private FileWriter impSessionFileWriter;
    private FileWriter assetFileWriter;


    @Override
    public void init() {
    }

    @Autowired
    @Override
    public void setProperties(DwProperties dwProperties) {
        this.dwProperties = dwProperties;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void dataWarehouseAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        this.impSessionFileWriter = new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("impsession") +
                "/impsession_" + System.currentTimeMillis() + ".json");
        this.assetFileWriter =
                new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("asset") + "/asset_" + System.currentTimeMillis() + ".json");
        log.info("本次处理开始时间：{}，结束时间：{}", startTime, endTime);
        List<Pair<String, String>> csvPaths = getCsvDataSet(startTime, endTime);
        long st = Instant.now().toEpochMilli();
        log.info("开始解析CSV数据...");
        List<List<Pair<String, String>>> partition = Lists.partition(csvPaths,
                csvPaths.size() / this.dwProperties.getExecutor() + 1);
        List<Future<ReorganizationDataWareGroup>> futures = new ArrayList<>();
        for (List<Pair<String, String>> pairs : partition) {
            futures.add(executorService.submit(new ReorganizationProducer(pairs)));
        }
        try {
            for (Future<ReorganizationDataWareGroup> future : futures) {
                ReorganizationDataWareGroup reorganizationDataWareGroup = future.get();
                List<AbstractDataWarehouseData> assetDataList = reorganizationDataWareGroup.getAssetDataList();
                List<AbstractDataWarehouseData> impsessionDataList =
                        reorganizationDataWareGroup.getImpsessionDataList();
                if (!CollectionUtils.isEmpty(assetDataList)) {
                    assetDataList.forEach(assetData -> {
                        assetFileWriter.write(JSONObject.toJSONString(assetData));
                    });
                    assetCount += assetDataList.size();
                }
                if (!CollectionUtils.isEmpty(impsessionDataList)) {
                    impsessionDataList.forEach(impsessionData -> {
                        impSessionFileWriter.write(JSONObject.toJSONString(impsessionData));
                    });
                    impSessionCount += assetDataList.size();
                }
            }
        } catch (Exception e) {
            log.error("解析过程中出现问题");
        }
        free();
        log.info("共用时{}毫秒", (Instant.now().toEpochMilli() - st));
    }


    /**
     * 获取csv文件名的集合 集合中一组pair中key：sessionCategory;
     * value：文件名 同一个sessionCategory可能对应多组文件 例: [ {key1:value1} {key1:value2} {key2:value3} ]
     */
    public List<Pair<String, String>> getCsvDataSet(LocalDateTime startTime, LocalDateTime endTime) {
        List<Pair<String, String>> result = new ArrayList<>();
        for (String sessionCategory : sesssionCategorys) {
            List<String> fileNames = getCsvDataSetBySessionCategory(sessionCategory, startTime, endTime);
            log.info("获取{}的csv文件[{}]个", sessionCategory, fileNames.size());
            for (String fileName : fileNames) {
                result.add(new Pair<>(sessionCategory, fileName));
            }
        }
        return result;
    }

    @Override
    public void free() {
        this.impSessionFileWriter.close();
        this.assetFileWriter.close();
        log.info("解析完成，共获取重点目标会话：[{}]条", impSessionCount);
        log.info("解析完成，共获取资产会话：[{}]条", assetCount);
        impSessionCount = assetCount = 0;
    }

    private boolean isImpSession(CsvRow csvRow) {
        return StringUtils.isNotEmpty(csvRow.get(HeadConst.FIELD.TARGET_NAME));
    }


    public class ReorganizationProducer implements Callable<ReorganizationDataWareGroup> {

        private final List<Pair<String, String>> csvPaths;

        private final ReorganizationDataWareGroup result;

        public ReorganizationProducer(List<Pair<String, String>> csvPaths) {
            this.csvPaths = csvPaths;
            result = ReorganizationDataWareGroup.init();
        }

        @Override
        public ReorganizationDataWareGroup call() throws Exception {
            for (Pair<String, String> csvPath : csvPaths) {
                CsvReader csvReader;
                try {
                    csvReader = CsvReader.builder().file(csvPath.getValue()).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    continue;
                }
                analysis(csvPath.getKey(), csvReader);
            }
            return result;
        }

        public void analysis(String sessionCategory, CsvReader csvReader) {
            CsvRow csvRow;
            while ((csvRow = csvReader.nextRow()) != null) {
                if (ReorganizationReceiver.this.impSessionCsvFilter.filter(csvRow)) {
                    AbstractDataWarehouseData impsessonData =
                            reorganizationFactory.getSessionFactory().create(sessionCategory, csvRow);
                    if (impsessonData != null) {
                        result.getImpsessionDataList().add(impsessonData);
                    }
                }
                if (ReorganizationReceiver.this.assetCsvFilter.filter(csvRow)) {
                    AbstractDataWarehouseData assetData =
                            reorganizationFactory.getSessionFactory().create(sessionCategory, csvRow);
                    if (assetData != null) {
                        result.getAssetDataList().add(assetData);
                    }
                }
            }
        }
    }


}
