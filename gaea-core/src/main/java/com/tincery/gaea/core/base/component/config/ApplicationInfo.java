package com.tincery.gaea.core.base.component.config;

import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ApplicationInfo {

    private static final Map<String, String> GLOBAL_MAP = new HashMap<>();
    private static final String APPLICATION_NAME = "applicationName";
    private static final String LAYER = "layer";
    private static final String CATEGORY = "category";
    private static Map<String, String> APPLICATION_MAP;
    private static volatile boolean lock = false;

    private ApplicationInfo() {
        throw new RuntimeException();
    }

    public static void init(String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            log.error("加载applicationName失败");
            throw new InitException("加载applicationName失败  请检查[spring.application.name]配置");
        }
        String[] buffer = applicationName.split("_");
        if (buffer.length != 2) {
            log.error("加载applicationName失败");
            throw new InitException("加载applicationName失败  请检查[spring.application.name]配置");
        }
        GLOBAL_MAP.put(APPLICATION_NAME, applicationName);
        GLOBAL_MAP.put(LAYER, buffer[0]);
        GLOBAL_MAP.put(CATEGORY, buffer[1]);
        lock();
    }


    /****
     * 初始化完成 锁定此map不能修改
     * @author gxz
     * @date 2020/8/18
     **/
    public static void lock() {
        if (lock) {
            log.error("上锁之后又被锁定");
          //  throw new RuntimeException("已经上锁");
        } else {
            APPLICATION_MAP = Collections.unmodifiableMap(GLOBAL_MAP);
            log.info("初始化完成 节点信息锁定");
            lock = true;
        }
    }

    public static String getApplicationName() {
        return APPLICATION_MAP.get(APPLICATION_NAME);
    }

    public static String getLayer() {
        return APPLICATION_MAP.get(LAYER);
    }

    public static String getCategory() {
        return APPLICATION_MAP.get(CATEGORY);
    }

    public static String getDataWarehouseCsvPathByCategory() {
        return NodeInfo.getDataWarehouseCsvPathByCategory(getCategory());
    }

    public static String getDataWarehouseJsonPathByCategory() {
        return NodeInfo.getDataWarehouseJsonPathByCategory(getCategory());
    }

    public static String getDataWarehouseCustomPathByCategory() {
        return NodeInfo.getDataWarehouseCustomPathByCategory(getCategory());
    }

    public static String getCachePathByCategory() {
        return NodeInfo.getCacheByCategory(getCategory());
    }

    public static String getCacheByCategory() {
        return NodeInfo.getCacheByCategory(getCategory());
    }

    public static String getBakByCategory() {
        return NodeInfo.getBakByCategory(getCategory());
    }

    public static String getErrorByCategory() {
        return NodeInfo.getErrorByCategory(getCategory());
    }

}
