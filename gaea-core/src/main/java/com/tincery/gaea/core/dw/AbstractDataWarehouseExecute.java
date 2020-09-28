package com.tincery.gaea.core.dw;

import com.tincery.gaea.core.base.component.Execute;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static com.tincery.gaea.core.base.tool.util.DateUtils.DAY;
import static com.tincery.gaea.core.base.tool.util.DateUtils.MINUTE;

/**
 * @author gxz gongxuanzhang@foxmail.com 此模块内容形式： 默认执行 将一行CSV传递给子类 子类需要对CSV进行处理
 **/
@Slf4j
public abstract class AbstractDataWarehouseExecute implements Execute {


    private List<CsvFilter> csvFilterList;

    public static void main(String[] args) {
        String a = "D:\\data\\datawarehouse\\csv\\ssl\\20200813/ssl_202008131224.TINCERY_101.csv";
        List<CsvFilter> csvFilterList = null;
        try {
            CsvReader build = CsvReader.builder().file(a).registerFilter(csvFilterList).build();
            CsvRow csvRow = null;
            while ((csvRow = build.nextRow()) != null) {
                System.out.println(csvRow);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        List<Pair<String, String>> csvPaths = getCsvDataSet();
        long startTime = Instant.now().toEpochMilli();
        log.info("开始解析CSV数据...");
        for (Pair<String, String> csvPath : csvPaths) {
            CsvReader csvReader;
            try {
                csvReader = CsvReader.builder().file(csvPath.getValue()).registerFilter(csvFilterList).build();
            } catch (IllegalAccessException e) {
                log.error("CSV读取失败");
                continue;
            }
            analysis(csvPath.getKey(), csvReader);
        }
        free();
        log.info("共用时{}毫秒", (Instant.now().toEpochMilli() - startTime));
    }


    public void free() {
        throw new UnsupportedOperationException();
    }

    public void analysis(String sessionCategory, CsvReader csvReader) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取csv文件名的集合 集合中一组pair中key：sessionCategory;value：文件名 同一个sessionCategory可能对应多组文件 例: [ {key1:value1} {key1:value2}
     * {key2:value3} ]
     */
    public abstract List<Pair<String, String>> getCsvDataSet();

    protected List<CsvFilter> registryFilter(CsvFilter... csvFilterList) {
        if (null == this.csvFilterList) {
            this.csvFilterList = new ArrayList<>();
        }
        this.csvFilterList.addAll(Arrays.asList(csvFilterList));
        return this.csvFilterList;
    }

}
