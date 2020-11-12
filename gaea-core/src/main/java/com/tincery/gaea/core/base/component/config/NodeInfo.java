package com.tincery.gaea.core.base.component.config;

import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: gxz gongxuanzhang@foxmail.com
 * HOME
 * ├── config #各模块运行所需配置文件，不用修改
 * │   ├── dgaSource
 * │   ├── geo2ip
 * │   ├── systemRule
 * │   └── trainingResultSet
 * └── log
 * ├── error
 * └── standard
 * <p>
 * DATA    # 数据路径，如果不配置默认写在$HOME/data
 * ├── dataWarehouse   #数据仓库路径
 * │   ├── csv
 * │   │   ├── session
 * │   │   └── more...
 * │   ├── json
 * |   │   ├── alarmMaterial
 * │   │   ├── impSession
 * │   │   └── more...
 * │   └── custom
 * │       ├── analysisPileLog
 * │       └── more...
 * ├── bak # 数据备份路径
 * │   ├── session
 * │   ├── ssl
 * │   └── more...
 * └── cache   # 缓存的json数据，由logstash统一进行入库
 *    ├── eventData
 *    ├── alarm
 *    └── more...
 * <p>
 * SRC    # 探针输出路径，gaea数据输入起点
 * ├── session
 * ├── ssl
 * └── more...
 **/
@Slf4j
public class NodeInfo {

    /**
     * AES加密解密密码
     */
    public final static String AES_PASSWORD = "Insomnia!@23";
    private static final Map<String, String> GLOBAL_MAP = new HashMap<>();
    private static final String NODE_NAME = "nodeName";
    private static final String HOME = "nodeHome";
    private static final String CONFIG = "configPath";
    private static final String DATA = "data";
    private static final String SRC_DATA = "srcPath";
    private static final String DATA_WAREHOUSE = "dataWarehouse";
    private static final String DATA_WAREHOUSE_CSV = "dataWarehouseCsv";
    private static final String DATA_WAREHOUSE_JSON = "dataWarehouseJson";
    private static final String DATA_WAREHOUSE_CUSTOM = "dataWarehouseCustom";
    private static final String BAK = "bak";
    private static final String CACHE = "cache";
    private static final String ERROR = "error";
    private static Map<String, String> NODE_MAP;
    private static volatile boolean lock = false;


    private NodeInfo() {
        throw new RuntimeException();
    }

    public static void init(String nodeName, String home, String srcPath, String dataPath) {
        if (StringUtils.isEmpty(home)) {
            log.error("加载home失败");
            throw new InitException("加载home失败,请检查[node.home]配置");
        }
        if (StringUtils.isEmpty(nodeName)) {
            log.error("加载nodeName失败");
            throw new InitException("加载nodeName失败,请检查[node.name]配置");
        }
        if (StringUtils.isEmpty(srcPath)) {
            log.error("加载srcPath失败");
            throw new InitException("加载srcPath失败,请检查[node.src-path]配置");
        }
        if (StringUtils.isEmpty(dataPath)) {
            dataPath = home + "/data";
            log.warn("加载数据路径失败，将默认分配路径[{}]", dataPath);
        }
        String configPath = home + "/config";
        String dataWarehousePath = dataPath + "/datawarehouse";
        String dataWarehouseCsvPath = dataWarehousePath + "/csv";
        String dataWarehouseJsonPath = dataWarehousePath + "/json";
        String dataWarehouseCustomPath = dataWarehousePath + "/custom";
        String bakPath = dataPath + "/bak";
        String cachePath = dataPath + "/cache";
        String errorPath = dataPath + "/error";
        FileUtils.checkPath(dataWarehouseCsvPath, dataWarehouseJsonPath, dataWarehouseCustomPath, bakPath, cachePath, errorPath);

        GLOBAL_MAP.put(NODE_NAME, nodeName);
        GLOBAL_MAP.put(HOME, home);
        GLOBAL_MAP.put(CONFIG, configPath);
        GLOBAL_MAP.put(SRC_DATA, srcPath);
        GLOBAL_MAP.put(DATA, dataPath);
        GLOBAL_MAP.put(DATA_WAREHOUSE, dataWarehousePath);
        GLOBAL_MAP.put(DATA_WAREHOUSE_CSV, dataWarehouseCsvPath);
        GLOBAL_MAP.put(DATA_WAREHOUSE_JSON, dataWarehouseJsonPath);
        GLOBAL_MAP.put(DATA_WAREHOUSE_CUSTOM, dataWarehouseCustomPath);
        GLOBAL_MAP.put(BAK, bakPath);
        GLOBAL_MAP.put(CACHE, cachePath);
        GLOBAL_MAP.put(ERROR, errorPath);
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
            // throw new RuntimeException("已经上锁");
        } else {
            NODE_MAP = Collections.unmodifiableMap(GLOBAL_MAP);
            log.info("初始化完成 节点信息锁定");
            lock = true;
        }
    }

    public static String getHome() {
        return NODE_MAP.get(HOME);
    }

    public static String getConfig() {
        return NODE_MAP.get(CONFIG);
    }

    public static String getNodeName() {
        return NODE_MAP.get(NODE_NAME);
    }

    public static String getSrcData() {
        return NODE_MAP.get(SRC_DATA);
    }

    public static String getData() {
        return NODE_MAP.get(DATA);
    }

    public static String getDataWarehousePath() {
        return NODE_MAP.get(DATA_WAREHOUSE);
    }

    public static String getDataWarehouseCsvPath() {
        return NODE_MAP.get(DATA_WAREHOUSE_CSV);
    }

    public static String getDataWarehouseCsvPathByCategory(String category) {
        String path = getDataWarehouseCsvPath() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getDataWarehouseJsonPath() {
        return NODE_MAP.get(DATA_WAREHOUSE_JSON);
    }

    public static String getDataWarehouseJsonPathByCategory(String category) {
        String path = getDataWarehouseJsonPath() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getDataWarehousePathCustom() {
        return NODE_MAP.get(DATA_WAREHOUSE_CUSTOM);
    }

    public static String getDataWarehouseCustomPathByCategory(String category) {
        String path = getDataWarehousePathCustom() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getCache() {
        return NODE_MAP.get(CACHE);
    }

    public static String getCacheByCategory(String category) {
        String path = getCache() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getBak() {
        return NODE_MAP.get(BAK);
    }

    public static String getBakByCategory(String category) {
        String path = getBak() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getDataMarketBakByCategory(String category) {
        String path = getBak() + "/dataMarket/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getError() {
        return NODE_MAP.get(ERROR);
    }

    public static String getErrorByCategory(String category) {
        String path = getError() + "/" + category + "/";
        FileUtils.checkPath(path);
        return path;
    }

    public static String getAlarmMaterial() {
        return getCacheByCategory("alarmMaterial");
    }

    public static String getEventData() {
        return getCacheByCategory("eventData");
    }

}
