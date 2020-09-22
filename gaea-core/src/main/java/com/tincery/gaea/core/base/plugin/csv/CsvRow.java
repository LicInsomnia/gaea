package com.tincery.gaea.core.base.plugin.csv;


import com.tincery.gaea.core.base.tool.util.StringUtils;

import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CsvRow {

    final Map<String, Integer> headerMap;

    final String[] fields;


    public CsvRow(Map<String, Integer> headerMap, String line) {
        this.headerMap = headerMap;
        this.fields = StringUtils.FileLineSplit(line);
    }

    public String get(String name) {
        return getOrDefault(headerMap.getOrDefault(name, -1),null);
    }

    public String get(int index) {
        return getOrDefault(index,null);
    }

    public String getOrDefault(String name,String defaultValue){
        return getOrDefault(headerMap.getOrDefault(name, -1),defaultValue);
    }
    public String getOrDefault(int index, String defaultValue){
        return index < 0 || index > fields.length - 1 ? defaultValue : fields[index];
    }


    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("CscRow:[");
        for (int i = 0; i < this.fields.length; i++) {
            if(i!=0){
                toString.append(",").append(fields[i]);
            }else{
                toString.append(fields[i]);
            }
        }
        toString.append("]");
        return toString.toString();
    }
}
