package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.ThPredicate;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * assetconfig的工具类
 **/
public class AssetConfigs {

    private static final BiFunction<JSONObject, AssetConfigDO, AlarmMaterialData> createAlarm;

    private static final Function<JSONObject, Integer> getProtocol;

    private static final Function<JSONObject, Integer> getPort;

    static {
        createAlarm = (jsonObject, assetConfigDO) -> new AlarmMaterialData();
        getProtocol = jsonObject -> jsonObject.getIntValue(HeadConst.FIELD.PROTOCOL);
        getPort = jsonObject -> jsonObject.getIntValue(HeadConst.FIELD.SERVER_PORT);
    }

    /****
     * 作为客户端是一条资产的时候调用此方法  此方法将返回方法所检测的所有告警素材
     *
     * @param assetJson 一条资产json
     * @param assetDetector 资产检测器
     * @return 产生的报警报告内容
     **/
    public static List<AlarmMaterialData> detectorClient(JSONObject assetJson, AssetDetector assetDetector) {
        AssetConfigDO assetConfig = assetDetector.getAsset(assetJson.getLong(HeadConst.FIELD.CLIENT_IP_N));
        // 先判断黑名单 命中告警
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.OUT, Border.DOMESTIC)) {
            // 境内
            return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
        }
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.OUT, Border.OVERSEAS)) {
            // 境外
            return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
        }
        // 判断白名单 如果命中则直接返回
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.OUT, Border.DOMESTIC)) {
            return null;
        }
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.OUT, Border.OVERSEAS)) {
            return null;
        }
        // 白名单没返回 则告警
        return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
    }

    public static List<AlarmMaterialData> detectorServer(JSONObject assetJson, AssetDetector assetDetector) {
        AssetConfigDO assetConfig = assetDetector.getAsset(assetJson.getLong(HeadConst.FIELD.SERVER_IP_N));
        // 先判断黑名单 命中告警
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.IN, Border.DOMESTIC)) {
            // 境内
            return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
        }
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.IN, Border.OVERSEAS)) {
            // 境外
            return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
        }
        // 判断白名单 如果命中则直接返回
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.IN, Border.DOMESTIC)) {
            return null;
        }
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.IN, Border.OVERSEAS)) {
            return null;
        }
        // 白名单没返回 则告警
        return Collections.singletonList(createAlarm.apply(assetJson, assetConfig));
    }


    public static List<AlarmMaterialData> detectorClientAndServer(JSONObject assetJson, AssetDetector assetDetector) {
        List<AlarmMaterialData> result = new ArrayList<>();
        List<AlarmMaterialData> clientAlarm = detectorClient(assetJson, assetDetector);
        if (!CollectionUtils.isEmpty(clientAlarm)) {
            result.addAll(clientAlarm);
        }
        List<AlarmMaterialData> serverAlarm = detectorServer(assetJson, assetDetector);
        if (!CollectionUtils.isEmpty(serverAlarm)) {
            result.addAll(serverAlarm);
        }
        return result;

    }

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/gongxuanzhang/Downloads/asset_1602654519220(2).json");
        FileWriter fileWriter = new FileWriter(new File("aaaa.json"));
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            JSONObject parse = (JSONObject) JSON.parse(line);
            parse.put(HeadConst.FIELD.CLIENT_IP_N, ToolUtils.IP2long(parse.getString("clientIp")));
            parse.put(HeadConst.FIELD.SERVER_IP_N, ToolUtils.IP2long(parse.getString("serverIp")));
            fileWriter.write(parse.toString() + "\n");
            fileWriter.flush();
        }
    }

    public static boolean check(JSONObject assetJson, AssetConfigDO assetConfig,
                                ListType listType, OutInput outInput, Border border) {
        AssetConfigDO.BlackOrWhiteList blackOrWhiteList = listType.function.apply(assetConfig);
        AssetConfigDO.OutInputFilter outInputFilter = outInput.getOutInputFunction.apply(blackOrWhiteList);
        return border.getHitableFunction.test(assetJson, outInputFilter, outInput.getIpFunction);

    }


    public enum ListType {
        BLACK(AssetConfigDO::getBlackList),
        WHITE(AssetConfigDO::getWhiteList);

        private final Function<AssetConfigDO, AssetConfigDO.BlackOrWhiteList> function;

        ListType(Function<AssetConfigDO, AssetConfigDO.BlackOrWhiteList> function) {
            this.function = function;
        }
    }

    public enum OutInput {
        OUT(AssetConfigDO.BlackOrWhiteList::getOut, (asset) -> asset.getLong(HeadConst.FIELD.SERVER_IP_N)),
        IN(AssetConfigDO.BlackOrWhiteList::getIn, (asset) -> asset.getLong(HeadConst.FIELD.CLIENT_IP_N));

        private final Function<AssetConfigDO.BlackOrWhiteList, AssetConfigDO.OutInputFilter> getOutInputFunction;

        private final Function<JSONObject, Long> getIpFunction;


        OutInput(Function<AssetConfigDO.BlackOrWhiteList, AssetConfigDO.OutInputFilter> getOutInputFunction,
                 Function<JSONObject
                         , Long> getIpFunction) {
            this.getOutInputFunction = getOutInputFunction;
            this.getIpFunction = getIpFunction;
        }

    }

    public enum Border {
        DOMESTIC(AssetConfigs::domesticHit),
        OVERSEAS(AssetConfigs::overseasHit);

        public ThPredicate<JSONObject, AssetConfigDO.OutInputFilter, Function<JSONObject, Long>> getHitableFunction;

        Border(ThPredicate<JSONObject, AssetConfigDO.OutInputFilter, Function<JSONObject, Long>> getHitableFunction) {
            this.getHitableFunction = getHitableFunction;
        }
    }

    private static boolean overseasHit(JSONObject assetJson, AssetConfigDO.OutInputFilter outInputFilter,
                                       Function<JSONObject, Long> getIp) {
        AssetConfigDO.OverseasFilter overseas = outInputFilter.getOverseas();
        if(overseas==null){
            return false;
        }
        return overseas.hit(getIp.apply(assetJson), getProtocol.apply(assetJson),
                getPort.apply(assetJson));
    }

    private static boolean domesticHit(JSONObject assetJson, AssetConfigDO.OutInputFilter outInputFilter,
                                       Function<JSONObject, Long> getIp) {
        List<AssetConfigDO.DomesticFilter> domestic = outInputFilter.getDomestic();
        if(CollectionUtils.isEmpty(domestic)){
            return false;
        }
        return domestic.stream().anyMatch(domesticFilter ->
                        domesticFilter.hit(getIp.apply(assetJson), getProtocol.apply(assetJson),
                                getPort.apply(assetJson)));

    }






}
