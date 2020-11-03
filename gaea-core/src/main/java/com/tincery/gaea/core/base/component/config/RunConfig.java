package com.tincery.gaea.core.base.component.config;

import com.alibaba.fastjson.JSONObject;

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


    public static boolean isEmpty() {
        return null == RUN_CONFIG || RUN_CONFIG.isEmpty();
    }

}
