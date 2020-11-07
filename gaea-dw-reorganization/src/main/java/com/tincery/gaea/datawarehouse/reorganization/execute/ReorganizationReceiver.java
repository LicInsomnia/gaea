package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.dw.AbstractDataWarehouseReceiver;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void dataWarehouseAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        this.impSessionFileWriter = new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("impsession") + "/impsession_" + System.currentTimeMillis() + ".json");
        this.assetFileWriter = new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("asset") + "/asset_" + System.currentTimeMillis() + ".json");
        super.dataWarehouseAnalysis(startTime, endTime);
    }

    @Override
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

    @Override
    public void analysis(String sessionCategory, CsvReader csvReader) {
        produce(sessionCategory, csvReader);
        countDownLatch.countDown();
    }

    public void produce(String sessionCategory, CsvReader csvReader) {
        CsvRow csvRow;
        while ((csvRow = csvReader.nextRow()) != null) {
            if (this.impSessionCsvFilter.filter(csvRow)) {
                AbstractDataWarehouseData impsessonData = reorganizationFactory.getSessionFactory().create(sessionCategory, csvRow);
                if (impsessonData != null) {
                    impSessionFileWriter.write(JSONObject.toJSONString(impsessonData));
                    impSessionCount++;
                }
            }
            if (this.assetCsvFilter.filter(csvRow)) {
                AbstractDataWarehouseData assetData = reorganizationFactory.getSessionFactory().create(sessionCategory, csvRow);
                if (assetData != null) {
                    assetFileWriter.write(JSONObject.toJSONString(assetData));
                    assetCount++;
                }
            }
        }
    }

    private boolean isImpSession(CsvRow csvRow) {
        return StringUtils.isNotEmpty(csvRow.get(HeadConst.FIELD.TARGET_NAME));
    }


}
