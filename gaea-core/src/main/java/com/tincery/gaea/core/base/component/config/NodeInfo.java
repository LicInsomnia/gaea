package com.tincery.gaea.core.base.component.config;

import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class NodeInfo {

    private NodeInfo() {
        throw new RuntimeException();
    }


    private static Map<String, String> Node_map;

    private static final Map<String, String> GLOBAL_MAP = new HashMap<>();

    private static final String NODE_NAME = "nodeName";

    private static final String NODE_HOME = "nodeHome";

    private static final String COMMON_DATA = "commonDataPath";

    private static final String TEMP_DATA = "tempDataPath";

    private static final String EVENT_DATA = "eventDataPath";

    private static final String ALARM_DATA = "alarmPath";

    private static final String DATA = "dataPath";

    private static final String BACK_DATA = "backPath";

    private static final String ERROR_DATA = "errorPath";

    private static final String SRC_DATA = "srcPath";

    private static final String CATEGORY_KEY = "category";

    private static volatile boolean lock = false;


    /**
     * AES加密解密密码
     */
    public final static String AES_PASSWORD = "Insomnia!@23";

    public static void init(String nodeName, String category, String nodeHome) {
        if (StringUtils.isEmpty(category)) {
            log.error("加载category失败");
            throw new InitException("加载category失败  请检查[node.category]配置");
        }
        if (StringUtils.isEmpty(nodeHome)) {
            log.error("加载nodeHome失败");
            throw new InitException("加载nodeHome失败,请检查[node.home]配置");
        }
        if (StringUtils.isEmpty(nodeName)) {
            nodeName = UUID.randomUUID().toString();
            log.warn("加载节点名称失败，将随机分配名称[{}]", nodeName);
        }
        String commonDataPath = nodeHome + "/data";
        String srcPath = commonDataPath + "/src",
                tmpPath = commonDataPath + "/tmp",
                eventPath = tmpPath + "/alarm_eventData",
                alarmPath = tmpPath + "/alarmmaterial",
                dataPath = commonDataPath + "/data/" + category,
                errorPath = commonDataPath + "/err/" + category,
                backPath = commonDataPath + "/bak/" + category;
        FileUtils.checkPath(commonDataPath, srcPath, tmpPath, eventPath, alarmPath, dataPath, errorPath, backPath);
        GLOBAL_MAP.put(NODE_NAME, nodeName);
        GLOBAL_MAP.put(NODE_HOME, nodeHome);
        GLOBAL_MAP.put(CATEGORY_KEY, category);
        GLOBAL_MAP.put(COMMON_DATA, commonDataPath);
        GLOBAL_MAP.put(SRC_DATA, srcPath);
        GLOBAL_MAP.put(TEMP_DATA, tmpPath);
        GLOBAL_MAP.put(EVENT_DATA, eventPath);
        GLOBAL_MAP.put(ALARM_DATA, alarmPath);
        GLOBAL_MAP.put(DATA, dataPath);
        GLOBAL_MAP.put(ERROR_DATA, errorPath);
        GLOBAL_MAP.put(BACK_DATA, backPath);
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
            throw new RuntimeException("已经上锁");
        } else {
            Node_map = Collections.unmodifiableMap(GLOBAL_MAP);
            log.info("初始化完成 节点信息锁定");
            lock = true;
        }
    }

    public static String getNodeHome() {
        return Node_map.get(NODE_HOME);
    }


    public static String getCommonData() {
        return Node_map.get(COMMON_DATA);
    }


    public static String getNodeName() {
        return Node_map.get(NODE_NAME);
    }

    public static String getCategory() {
        return Node_map.get(CATEGORY_KEY);
    }

    public static String getErrorData() {
        return Node_map.get(ERROR_DATA);
    }

    public static String getSrcData() {
        return Node_map.get(SRC_DATA);
    }

    public static String getEventData() {
        return Node_map.get(EVENT_DATA);
    }

    public static String getTempData() {
        return Node_map.get(TEMP_DATA);
    }

    public static String getAlarmData() {
        return Node_map.get(ALARM_DATA);
    }

    public static String getBackData() {
        return Node_map.get(BACK_DATA);
    }

    public static String getData() {
        return Node_map.get(DATA);
    }

}
