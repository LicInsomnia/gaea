package com.tincery.gaea.core.reorganization;

import com.alibaba.fastjson.annotation.JSONField;
import com.tincery.gaea.core.base.component.Execute;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.starter.base.mgt.NodeInfo;
import com.tincery.starter.mgt.ConstManager;
import lombok.Getter;
import lombok.Setter;

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
 **/
public abstract class AbstractReorganizationExecute<ANALYSIS extends ReorganizationLineMultiAnalysis> implements Execute {

    private final ANALYSIS analysis;

    public AbstractReorganizationExecute(ANALYSIS analysis) {
        this.analysis = analysis;
    }

    @Override
    public void execute() {
        Map<String,Object> reorganization = (Map<String,Object>)ConstManager.getCommonConfig("reorganization");
        LocalDateTime starttime = DateUtils.Date2LocalDateTime((Date)reorganization.get("starttime"));
        Integer recolltime = (Integer)reorganization.get("recolltime");
        LocalDateTime endtime = starttime.plusMinutes(recolltime);
        long endTimeLong = DateUtils.LocalDateTime2Long(endtime);
        long startTimeLong = DateUtils.LocalDateTime2Long(starttime);
        List<String> csvDataSet = getCsvDataSet(startTimeLong, endTimeLong);

    }

    public static List<String> getCsvDataSet(long startTime, long endTime) {
        String rootPath = NodeInfo.getTinceryDataPath() +"/data/"+NodeInfo.getCategory();
        List<String> list = new ArrayList<>();
        long timeStamp = startTime = startTime / MINUTE * MINUTE;
        endTime = endTime /MINUTE * MINUTE + MINUTE;
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

    @Setter@Getter
    public static class DurationInfo{
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime starttime;
        /***分钟数*/
        private int recolltime;
    }




}
