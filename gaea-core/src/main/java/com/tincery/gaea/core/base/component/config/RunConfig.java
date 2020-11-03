package com.tincery.gaea.core.base.component.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

public class RunConfig {

    private static JSONObject RUN_CONFIG;

    private RunConfig() {

    }

    public static void init(Map<String, Object> configData) {
        RUN_CONFIG = new JSONObject(configData);
    }

    public static <T> T get(BiFunction<String, JSONObject, T> function, String key) {
        return function.apply(key, RUN_CONFIG);
    }

    public static String getString(String key) {
        return RUN_CONFIG.getString(key);
    }

    public static Integer getInteger(String key) {
        return RUN_CONFIG.getInteger(key);
    }

    public static Long getLong(String key) {
        return RUN_CONFIG.getLong(key);
    }

    public static Date getDate(String key) {
        return RUN_CONFIG.getDate(key);
    }

    public static Double getDouble(String key) {
        return RUN_CONFIG.getDouble(key);
    }

    public static JSONObject getJSONObject(String key) {
        return RUN_CONFIG.getJSONObject(key);
    }

    public static JSONArray getJSONArray(String key) {
        return RUN_CONFIG.getJSONArray(key);
    }

    public static Object getOrDefault(String key, Object obj) {
        return RUN_CONFIG.getOrDefault(key, obj);
    }

    public static boolean isEmpty() {
        return null == RUN_CONFIG || RUN_CONFIG.isEmpty();
    }

}
