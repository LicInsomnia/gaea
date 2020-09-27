package com.tincery.gaea.core.reorganization;

import com.tincery.gaea.core.base.component.Execute;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.tincery.gaea.core.base.tool.util.DateUtils.DAY;
import static com.tincery.gaea.core.base.tool.util.DateUtils.MINUTE;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 此模块内容形式：
 * 默认执行 将一行CSV传递给子类 子类需要对CSV进行处理
 **/
@Slf4j
public abstract class AbstractReorganizationExecute implements Execute {


    private List<CsvFilter> csvFilterList;

    @Override
    public void execute() {
        Map<String, Object> reorganization = (Map<String, Object>) CommonConfig.get("reorganization");
        LocalDateTime startTime = DateUtils.Date2LocalDateTime((Date) reorganization.get("starttime"));
        Integer recolltime = (Integer) reorganization.get("recolltime");
        LocalDateTime endTime = startTime.plusMinutes(recolltime);

        long endTimeLong = DateUtils.LocalDateTime2Long(endTime);
        long startTimeLong = DateUtils.LocalDateTime2Long(startTime);
        List<String> csvPaths = getCsvDataSet(startTimeLong, endTimeLong);
        for (String csvPath : csvPaths) {
            CsvReader csvReader;
            try {
                csvReader = CsvReader.builder().file(csvPath).registerFilter(csvFilterList).build();
            } catch (IllegalAccessException e) {
                log.error("CSV读取失败");
                return;
            }
            analysis(csvReader);
        }
        free();
    }


    public void free() {
        throw new UnsupportedOperationException();
    }

    public void analysis(CsvReader csvReader) {
        throw new UnsupportedOperationException();
    }

    public static List<String> getCsvDataSet(long startTime, long endTime) {
        String rootPath = NodeInfo.getCommonData() + "/data/" + NodeInfo.getCategory();
        List<String> list = new ArrayList<>();
        long timeStamp = startTime = startTime / MINUTE * MINUTE;
        endTime = endTime / MINUTE * MINUTE + MINUTE;
        while (timeStamp <= endTime) {
            File path = new File(rootPath + "/" + ToolUtils.stamp2Date(timeStamp, "yyyyMMdd"));
            if (path.exists() && path.isDirectory()) {
                String[] files = path.list();
                if (null != files) {
                    for (String fileName : files) {
                        if (!fileName.startsWith(NodeInfo.getCategory())) {
                            continue;
                        }
                        String[] elements = fileName.split("\\.")[0].split("_");
                        String timeStampStr = elements[elements.length - 1];
                        long ts = ToolUtils.date2Stamp(timeStampStr, "yyyyMMddHHmm");
                        if (startTime <= ts && endTime > ts) {
                            list.add(path + "/" + fileName);
                        }
                    }
                }
            }
            timeStamp += DAY;
        }
        return list;
    }


}
