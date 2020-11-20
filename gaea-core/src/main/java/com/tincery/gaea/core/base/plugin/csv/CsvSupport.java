package com.tincery.gaea.core.base.plugin.csv;

import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tincery.gaea.core.base.tool.util.DateUtils.DAY;
import static com.tincery.gaea.core.base.tool.util.DateUtils.MINUTE;

public class CsvSupport {

    public static List<String> getCsvDataSetBySessionCategory(String sessionCategory, LocalDateTime startTime, LocalDateTime endTime) {
        long endTimeLong = DateUtils.LocalDateTime2Long(endTime);
        long startTimeLong = DateUtils.LocalDateTime2Long(startTime);
        String rootPath = NodeInfo.getDataWarehouseCsvPathByCategory(sessionCategory);
        List<String> list = new ArrayList<>();
        long timeStamp = startTimeLong = startTimeLong / MINUTE * MINUTE;
        endTimeLong = endTimeLong / MINUTE * MINUTE + MINUTE;
        while (timeStamp <= endTimeLong) {
            File path = new File(rootPath + "/" + DateUtils.format(timeStamp, "yyyyMMdd"));
            if (path.exists() && path.isDirectory()) {
                String[] files = path.list();
                if (null != files) {
                    for (String fileName : files) {
                        if (!fileName.startsWith(sessionCategory)) {
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

}
