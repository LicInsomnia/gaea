package com.tincery.gaea.core.base.plugin.csv;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.tool.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CsvRow {

    final Map<String, Integer> headerMap;

    final String[] fields;

    private Map<String, Object> extension;


    public CsvRow(Map<String, Integer> headerMap, String line) {
        this.headerMap = headerMap;
        this.fields = StringUtils.FileLineSplit(line);
    }

    public String get(String name) {
        return get(headerMap.getOrDefault(name, -1));
    }

    /**
     * 如果获取的字段值为""则强制返回null
     *
     * @param name
     */
    public String getEmptyNull(String name) {
        String value = get(headerMap.getOrDefault(name, -1));
        if (StringUtils.isEmpty(value)) {
            value = null;
        }
        return value;
    }

    /**
     * 获取字段的值为""则强制返回默认值
     *
     * @param name
     */
    public String getEmptyDefault(String name, String defaultValue) {
        String value = get(headerMap.getOrDefault(name, -1));
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public String get(int index) {
        return getOrDefault(index, null);
    }

    public String getOrDefault(String name, String defaultValue) {
        return getOrDefault(headerMap.getOrDefault(name, -1), defaultValue);
    }

    public String getOrDefault(int index, String defaultValue) {
        return index < 0 || index > fields.length - 1 ? defaultValue : fields[index];
    }

    public Integer getInteger(String name) {
        try {
            return Integer.parseInt(get(name));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getInteger(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getIntegerOrDefault(String name, Integer defaultValue) {
        Integer integer = getInteger(name);
        return integer == null ? defaultValue : integer;
    }

    public Integer getIntegerOrDefault(int index, Integer defaultValue) {
        Integer integer = getInteger(index);
        return integer == null ? defaultValue : integer;
    }

    public Long getLong(String name) {
        try {
            return Long.parseLong(get(name));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLong(int index) {
        try {
            return Long.parseLong(get(index));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLongOrDefault(String name, Long defaultValue) {
        Long aLong = getLong(name);
        return aLong == null ? defaultValue : aLong;
    }

    public Long getLongOrDefault(int index, Long defaultValue) {
        Long aLong = getLong(index);
        return aLong == null ? defaultValue : aLong;
    }

    public Boolean getBoolean(String name) {
        return Boolean.valueOf(get(name));
    }

    public Boolean getBoolean(int index) {
        return Boolean.valueOf(get(index));
    }

    public JSONObject getJsonObject(String name) {
        String value = get(name);
        return null == value ? null : (JSONObject) JSONObject.parse(value);
    }

    public JSONObject getJsonObject(int index) {
        return (JSONObject) JSONObject.parse(get(index));
    }


    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("CscRow:[");
        for (int i = 0; i < this.fields.length; i++) {
            if (i != 0) {
                toString.append(",").append(fields[i]);
            } else {
                toString.append(fields[i]);
            }
        }
        toString.append("]");
        return toString.toString();
    }

    public CsvRow putExtension(String key, Object value) {
        if (this.extension == null) {
            this.extension = new HashMap<>(16);
        }
        this.extension.put(key, value);
        return this;
    }

    public Object getExtensionValue(String key) {
        if (this.extension == null) {
            return null;
        }
        return this.extension.get(key);
    }
}
