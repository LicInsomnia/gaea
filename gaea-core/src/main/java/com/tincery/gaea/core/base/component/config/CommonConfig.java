package com.tincery.gaea.core.base.component.config;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.exception.InitException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CommonConfig {
    private static Map<String, Object> COMMON_CONFIG = new HashMap<>();

    public static void put(String key, Object value) {
        COMMON_CONFIG.put(key, value);
    }

    public static Object get(String key) {
        return COMMON_CONFIG.get(key);
    }
    /*****
     * 把CommonConfig 和RunConfig 合并内容
     * 在已经加载完commonConfig后使用
     * @author gxz
     * @date 2020/8/15
     **/
    public static void mergeCommonRun(String runKey, Object runValue) {
        if (COMMON_CONFIG.containsKey(runKey)) {
            JSONObject commonValue = new JSONObject((Map) COMMON_CONFIG.get(runKey));
            commonValue.putAll(new JSONObject((Map) runValue));
        } else {
            COMMON_CONFIG.put(runKey, runValue);
        }
    }

    /****
     * 在完成初始化之后调用此方法 执行校验
     * @author gxz
     * @date 2020/8/15
     **/
    public static void validatorCommonConfig() {
        if (COMMON_CONFIG.isEmpty()) {
            String message = "commonConfig 没有内容 初始化失败";
            log.error(message);
            throw new InitException(message);
        }
        log.info("初始化commonConfig成功");
    }
}
