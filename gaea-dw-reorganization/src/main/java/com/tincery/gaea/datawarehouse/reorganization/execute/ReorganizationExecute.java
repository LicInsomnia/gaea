package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.dw.AbstractDataWarehouseExecute;
import com.tincery.gaea.core.dw.SessionFactory;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.tincery.gaea.core.base.tool.util.DateUtils.DAY;
import static com.tincery.gaea.core.base.tool.util.DateUtils.MINUTE;

@Component
@Slf4j
public class ReorganizationExecute extends AbstractDataWarehouseExecute {

    private final AssetCsvFilter assetCsvFilter;
    private final SessionFactory sessionFactory;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private FileWriter impSessionFileWriter;
    private FileWriter assetFileWriter;
    private static final String[] sesssionCategorys = {
            "session",
            "ssl",
            "openvpn",
            "dns",
            "http",
            "email",
            "isakmp",
            "ssh",
            "ftp_telnet",
            "esp_ah"
    };
    private static int impsessionCount = 0;
    private static int assetCount = 0;

    public ReorganizationExecute(AssetCsvFilter assetCsvFilter, SessionFactory sessionFactory) {
        this.assetCsvFilter = assetCsvFilter;
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void init() {
        Map<String, Object> reorganization = (Map<String, Object>) CommonConfig.get("reorganization");
        this.startTime = DateUtils.Date2LocalDateTime((Date) reorganization.get("starttime"));
        Integer recolltime = (Integer) reorganization.get("recolltime");
        this.endTime = startTime.plusMinutes(recolltime);
        this.registryFilter(
                new ImpSessionCsvFilter(),
                this.assetCsvFilter
        );
        impSessionFileWriter = new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("impsession") + "/impsession_" + System.currentTimeMillis() + ".json");
        assetFileWriter = new FileWriter(NodeInfo.getDataWarehouseJsonPathByCategory("asset") + "/asset_" + System.currentTimeMillis() + ".json");
    }

    @Override
    public List<Pair<String, String>> getCsvDataSet() {
        List<Pair<String, String>> result = new ArrayList<>();
        for (String sessionCategory : sesssionCategorys) {
            List<String> fileNames = getCsvDataSetBySessionCategory(sessionCategory);
            log.info("获取{}的csv文件{}个", sessionCategory, fileNames.size());
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
        log.info("解析完成，共获取重点目标会话：{}条", impsessionCount);
        log.info("解析完成，共获取资产会话：{}条", assetCount);
        impsessionCount = assetCount = 0;
    }

    @Override
    public void analysis(String sessionCategory, CsvReader csvReader) {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                CPU + 1,
                CPU * 2,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        executorService.execute(new ImpSessionProduce(sessionCategory, csvReader));
        executorService.execute(new AssetProduce(sessionCategory, csvReader));
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }


    private List<String> getCsvDataSetBySessionCategory(String sesionCategory) {
        long endTimeLong = DateUtils.LocalDateTime2Long(this.endTime);
        long startTimeLong = DateUtils.LocalDateTime2Long(this.startTime);
        String rootPath = NodeInfo.getDataWarehouseCsvPathByCategory(sesionCategory);
        List<String> list = new ArrayList<>();
        long timeStamp = startTimeLong = startTimeLong / MINUTE * MINUTE;
        endTimeLong = endTimeLong / MINUTE * MINUTE + MINUTE;
        while (timeStamp <= endTimeLong) {
            File path = new File(rootPath + "/" + ToolUtils.stamp2Date(timeStamp, "yyyyMMdd"));
            if (path.exists() && path.isDirectory()) {
                String[] files = path.list();
                if (null != files) {
                    for (String fileName : files) {
                        if (!fileName.startsWith(sesionCategory)) {
                            continue;
                        }
                        String[] elements = fileName.split("\\.")[0].split("_");
                        String timeStampStr = elements[elements.length - 1];
                        long ts = ToolUtils.date2Stamp(timeStampStr, "yyyyMMddHHmm");
                        if (startTimeLong <= ts && endTimeLong > ts) {
                            list.add(path + "/" + fileName);
                        }
                    }
                }
            }
            timeStamp += DAY;
        }
        return list;
    }


    public class ImpSessionProduce implements Runnable {

        private final String sessionCategory;
        private final CsvReader csvReader;

        public ImpSessionProduce(String sessionCategory, CsvReader csvReader) {
            this.sessionCategory = sessionCategory;
            this.csvReader = csvReader;
        }

        @Override
        public void run() {
            CsvRow csvRow;
            while ((csvRow = csvReader.nextRow(ImpSessionCsvFilter.class)) != null) {
                try {
                    AbstractDataWarehouseData abstractDataWarehouseData = sessionFactory.create(sessionCategory, csvRow);
                    impSessionFileWriter.write(JSONObject.toJSONString(abstractDataWarehouseData));
                    impsessionCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class AssetProduce implements Runnable {

        private final String sessionCategory;
        private final CsvReader csvReader;

        public AssetProduce(String sessionCategory, CsvReader csvReader) {
            this.sessionCategory = sessionCategory;
            this.csvReader = csvReader;
        }

        @Override
        public void run() {
            CsvRow csvRow;
            while ((csvRow = csvReader.nextRow(AssetCsvFilter.class)) != null) {
                try {
                    AbstractDataWarehouseData abstractDataWarehouseData = sessionFactory.create(sessionCategory, csvRow);
                    assetFileWriter.write(JSONObject.toJSONString(abstractDataWarehouseData));
                    assetCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
