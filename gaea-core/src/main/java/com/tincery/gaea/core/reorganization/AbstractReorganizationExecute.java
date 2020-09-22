/*
package com.tincery.gaea.core.reorganization;

import com.tincery.gaea.core.base.component.Execute;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.starter.base.mgt.NodeInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * @author gxz gongxuanzhang@foxmail.com
 **//*

public abstract class AbstractReorganizationExecute implements Execute {

    @Override
    public void execute() {

    }

    public static List<String> getCsvDataSet(String rootPath, String filePre, long startTime, long endTime) {
        String rootPath = NodeInfo.getTinceryDataPath() +"/data/"
        List<String> list = new ArrayList<>();
        long timeStamp = startTime = startTime / MINUTE * MINUTE;
        endTime = endTime / MINUTE * MINUTE + MINUTE;
        while (timeStamp <= endTime) {
            File path = new File(rootPath + "/" + ToolUtils.stamp2Date(timeStamp, "yyyyMMdd"));
            if (path.exists() && path.isDirectory()) {
                String[] files = path.list();
                if (null != files) {
                    for (String fileName : files) {
                        if (!fileName.startsWith(filePre)) {
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
*/
