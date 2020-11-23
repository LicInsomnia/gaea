package com.tincery.gaea.datamarket.asset.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.dm.*;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.dao.*;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;


/**
 * @author gxz
 */
@Slf4j
@Component
public class AssetReceiver extends AbstractDataMarketReceiver {

    private final List<AlarmMaterialData> alarmList = new CopyOnWriteArrayList<>();

    private final List<JSONObject> eventDataList = new CopyOnWriteArrayList<>();

    private static final int ALARM_WRITE_COUNT = 20000;

    protected static ThreadPoolExecutor executorService;

    static {
        executorService = new ThreadPoolExecutor(
                CPU + 1,
                CPU * 2,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(4096),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }


    @Autowired
    private AssetDetector assetDetector;

    @Autowired
    private AssetUnitDao assetUnitDao;

    @Autowired
    private AssetIpDao assetIpDao;

    @Autowired
    private AssetProtocolDao assetProtocolDao;

    @Autowired
    private AssetPortDao assetPortDao;

    @Autowired
    private AssetExtensionDao assetExtensionDao;

    private static final LongAdder longAdder = new LongAdder();

    @Override
    protected void dmFileAnalysis(File file) {
        List<String> allLines = FileUtils.readLine(file);
        List<JSONObject> clientAssetList = new ArrayList<>();
        List<JSONObject> serverAssetList = new ArrayList<>();
        int executor = this.dmProperties.getExecutor();
        if (executor <= 1) {
            // 单线程执行
            for (String line : allLines) {
                JSONObject assetJson = JSON.parseObject(line);
                alarmAdd(AssetFlag.jsonRun(assetJson, assetDetector), assetJson);
                AssetFlag.fillAndAdd(assetJson, assetDetector, clientAssetList, serverAssetList);
            }
        } else {
            // 多线程执行
            List<List<String>> partition = Lists.partition(allLines, allLines.size() / executor + 1);
            List<Future<AssetResult>> futures = new ArrayList<>();
            for (List<String> lines : partition) {
                futures.add(executorService.submit(new AssetProducer(lines)));
            }
            for (Future<AssetResult> future : futures) {
                try {
                    AssetResult assetResult = future.get();
                    clientAssetList.addAll(assetResult.clientAssetList);
                    serverAssetList.addAll(assetResult.serverAssetList);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }
        free(clientAssetList, serverAssetList);
    }

    @Override
    @Autowired
    protected void setDmProperties(DmProperties dmProperties) {
        this.dmProperties = dmProperties;
    }

    private void free(List<JSONObject> clientAssetList, List<JSONObject> serverAssetList) {
        List<JSONObject> allAsset = new ArrayList<>(clientAssetList);
        allAsset.addAll(serverAssetList);
        // 分组
        List<AssetDataDTO> unitList = AssetGroupSupport.getSaveDataByAll(allAsset, AssetGroupSupport::unitDataFrom);
        AssetGroupSupport.rechecking(assetUnitDao, unitList);
        unitList.forEach(assetUnitDao::saveOrUpdate);
        log.info("单位维度合并插入{}条数据", unitList.size());
        List<AssetDataDTO> ipList = AssetGroupSupport.getSaveDataByServerAndClient(clientAssetList
                , AssetGroupSupport::clientIpDataFrom, serverAssetList, AssetGroupSupport::serverIpDataFrom);
        AssetGroupSupport.rechecking(assetIpDao, ipList);
        ipList.forEach(assetIpDao::saveOrUpdate);
        log.info("IP维度合并插入{}条数据", ipList.size());

        List<AssetDataDTO> protocolList = AssetGroupSupport.getSaveDataByServerAndClient(clientAssetList,
                AssetGroupSupport::clientProtocolDataFrom, serverAssetList, AssetGroupSupport::serverProtocolDataFrom);
        assetProtocolDao.insert(protocolList);
        log.info("协议维度合并插入{}条数据", protocolList.size());

        List<AssetDataDTO> portData = AssetGroupSupport.getSaveDataByAll(serverAssetList,
                AssetGroupSupport::portDataFrom);
        assetPortDao.insert(portData);
        log.info("端口维度合并插入{}条数据", portData.size());

        List<AssetExtension> extensionList
                = AssetGroupSupport.getSaveExtension(allAsset,
                AssetExtension::fromAssetJsonObject);
        AssetGroupSupport.rechecking(assetExtensionDao, extensionList);
        extensionList.forEach(assetExtensionDao::saveOrUpdate);
        log.info("拓展信息合并插入{}条数据", extensionList.size());

        writeAlarm();
        writeEventData();
    }


    private synchronized void alarmAdd(List<AlarmMaterialData> alarmMaterialDataList, JSONObject jsonObject) {
        if (CollectionUtils.isEmpty(alarmMaterialDataList)) {
            return;
        }
        this.alarmList.addAll(alarmMaterialDataList);
        this.eventDataList.add(jsonObject);
        if (this.alarmList.size() > ALARM_WRITE_COUNT) {
            writeAlarm();
        }
        if (this.eventDataList.size() > ALARM_WRITE_COUNT) {
            writeEventData();
        }
    }

    public synchronized void writeEventData() {
        if (eventDataList.isEmpty()) {
            return;
        }
        String fileName = "assetAlarm" + System.currentTimeMillis() + ".json";
        File file = new File(NodeInfo.getEventData() + fileName);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            for (JSONObject jsonObject : this.eventDataList) {
                fileWriter.write(jsonObject.toJSONString() + "\n");
            }
            this.eventDataList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeAlarm() {
        String fileName = "assetAlarm" + Instant.now().toEpochMilli() + ".json";
        File file = new File(NodeInfo.getAlarmMaterial() + fileName);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            for (AlarmMaterialData alarmMaterialData : this.alarmList) {
                fileWriter.write(JSON.toJSONString(alarmMaterialData) + "\n");
            }
            this.alarmList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init() {

    }

    public enum AssetFlag {

        NOT_ASSET(0, (json, detector) -> null),
        CLIENT_ASSET(1, AssetConfigs::detectorClient),
        SERVER_ASSET(2, AssetConfigs::detectorServer),
        SERVER_AND_CLIENT_ASSET(3, AssetConfigs::detectorClientAndServer);

        private final int flag;

        private final BiFunction<JSONObject, AssetDetector, List<AlarmMaterialData>> function;

        AssetFlag(int flag, BiFunction<JSONObject, AssetDetector, List<AlarmMaterialData>> function) {
            this.flag = flag;
            this.function = function;
        }

        private static AssetFlag findByFlag(int flag) {
            Optional<AssetFlag> first = Arrays.stream(values()).filter(assetFlag -> assetFlag.flag == flag).findFirst();
            return first.orElse(null);
        }



        public static List<AlarmMaterialData> jsonRun(JSONObject assetJson, AssetDetector assetDetector) {
            int flag = assetJson.getIntValue("assetFlag");
            return findByFlag(flag).function.apply(assetJson, assetDetector);
        }

        public static void fillAndAdd(JSONObject assetJson, AssetDetector assetDetector, List<JSONObject> clientList,
                                      List<JSONObject> serverList) {
            long clientIp = assetJson.getLong(HeadConst.FIELD.CLIENT_IP_N);
            AssetConfigDO clientConfig = assetDetector.getAsset(clientIp);
            long serverIp = assetJson.getLong(HeadConst.FIELD.SERVER_IP_N);
            AssetConfigDO serverConfig = assetDetector.getAsset(serverIp);
            switch (findByFlag(assetJson.getIntValue("assetFlag"))) {
                case CLIENT_ASSET:
                    if (clientConfig != null) {
                        fillAsset(assetJson, clientConfig, clientIp);
                        clientList.add(assetJson);
                    }
                    break;
                case SERVER_ASSET:
                    if (serverConfig != null) {
                        fillAsset(assetJson, serverConfig, serverIp);
                        serverList.add(assetJson);
                    }
                    break;
                case SERVER_AND_CLIENT_ASSET:
                    if (clientConfig != null) {
                        fillAsset(assetJson, clientConfig, clientIp);
                        clientList.add(assetJson);
                    }
                    JSONObject clone = (JSONObject) assetJson.clone();
                    if (serverConfig != null) {
                        fillAsset(clone, serverConfig, serverIp);
                        serverList.add(clone);
                    }

                default:
                    break;
            }
            longAdder.increment();
            System.out.println(longAdder.intValue());
        }

        private static void fillAsset(JSONObject assetJson, AssetConfigDO config, long targetIp) {
            assetJson.put("ip", targetIp);
            assetJson.put("unit", config.getUnit());
            assetJson.put("name", config.getName());
            assetJson.put("alarm", false);
        }
    }

    private static class AssetResult {
        private final List<JSONObject> clientAssetList;
        private final List<JSONObject> serverAssetList;

        public AssetResult(List<JSONObject> clientAssetList, List<JSONObject> serverAssetList) {
            this.clientAssetList = clientAssetList;
            this.serverAssetList = serverAssetList;
        }
    }

    private class AssetProducer implements Callable<AssetResult> {

        final private List<String> lines;

        private AssetProducer(List<String> lines) {
            this.lines = lines;
        }

        @Override
        public AssetResult call() {
            List<JSONObject> clientAssetList = new ArrayList<>();
            List<JSONObject> serverAssetList = new ArrayList<>();
            for (String line : lines) {
                if (line.isEmpty()) {
                    continue;
                }
                JSONObject assetJson = null;
                try {
                    assetJson = JSON.parseObject(line);
                } catch (Exception e) {
                    log.error("资产会话JSON序列化失败，错误JSON：{}", line);
                    continue;
                }
                AssetReceiver.this.alarmAdd(AssetFlag.jsonRun(assetJson, assetDetector), assetJson);
                AssetFlag.fillAndAdd(assetJson, assetDetector, clientAssetList, serverAssetList);
            }
            return new AssetResult(clientAssetList, serverAssetList);
        }
    }

}
