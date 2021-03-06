package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.TeFunction;
import com.tincery.gaea.api.base.TePredicate;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * assetconfig的工具类
 **/
public class AssetConfigs {

    private static final TeFunction<JSONObject, AssetConfigDO, Boolean, AlarmMaterialData> CREATE_ALARM;

    private static final Function<JSONObject, Integer> GET_PROTOCOL;

    private static final Function<JSONObject, Integer> GET_PORT;

    static {
        CREATE_ALARM = AlarmMaterialData::new;
        GET_PROTOCOL = jsonObject -> jsonObject.getIntValue(HeadConst.FIELD.PROTOCOL);
        GET_PORT = jsonObject -> jsonObject.getIntValue(HeadConst.FIELD.SERVER_PORT);
    }




    /***
     * ***********************************************************
     * ***********          各种检测                          ******
     * ***********************************************************
     **/


    /****
     * 作为客户端是一条资产的时候调用此方法  此方法将返回方法所检测的所有告警素材
     *
     * @param assetJson 一条资产json
     * @param assetDetector 资产检测器
     * @return 产生的报警报告内容
     **/
    public static List<AlarmMaterialData> detectorClient(JSONObject assetJson, AssetDetector assetDetector) {
        AssetConfigDO assetConfig = assetDetector.getAsset(assetJson.getLong(HeadConst.FIELD.CLIENT_IP_N));
        if(assetConfig==null){
            return null;
        }
        assetJson.put("isClient", true);
        if(assetConfig.isRange()){
            assetJson.merge("alarm",AssetDataDTO.NEW_IP,(alarm,alarmLong)->(long)alarmLong | AssetDataDTO.NEW_IP);
        }
        // 先判断黑名单 命中告警
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.OUT, Border.DOMESTIC)) {
            // 境内
            return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, true));
        }
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.OUT, Border.OVERSEAS)) {
            // 境外
            return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, true));
        }
        // 判断白名单 如果命中则直接返回
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.OUT, Border.DOMESTIC)) {
            return null;
        }
        if (check(assetJson, assetConfig,   ListType.WHITE, OutInput.OUT, Border.OVERSEAS)) {
            return null;
        }
        // 白名单没返回 则告警
        return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, true));
    }

    public static List<AlarmMaterialData> detectorServer(JSONObject assetJson, AssetDetector assetDetector) {
        AssetConfigDO assetConfig = assetDetector.getAsset(assetJson.getLong(HeadConst.FIELD.SERVER_IP_N));
        assetJson.put("isClient", false);
        if (null == assetConfig) {
            return null;
        }
        if(assetConfig.isRange()){
            assetJson.merge("alarm",AssetDataDTO.NEW_IP,(alarm,alarmLong)->(long)alarmLong | AssetDataDTO.NEW_IP);
        }
        // 客户端需要匹配密码
        assetConfig.strategyHit(assetJson);
        // 先判断黑名单 命中告警
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.IN, Border.DOMESTIC)) {
            // 境内
            return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, false));
        }
        if (check(assetJson, assetConfig, ListType.BLACK, OutInput.IN, Border.OVERSEAS)) {
            // 境外
            return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, false));
        }
        // 判断白名单 如果命中则直接返回
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.IN, Border.DOMESTIC)) {
            return null;
        }
        if (check(assetJson, assetConfig, ListType.WHITE, OutInput.IN, Border.OVERSEAS)) {
            return null;
        }
        // 白名单没返回 则告警
        return Collections.singletonList(CREATE_ALARM.apply(assetJson, assetConfig, false));
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

    /**
     * ***********************************************************
     * ***********          support                          ******
     * ***********************************************************
     **/

    private static boolean check(JSONObject assetJson, AssetConfigDO assetConfig,
                                 ListType listType, OutInput outInput, Border border) {
        AssetConfigDO.BlackOrWhiteList blackOrWhiteList = listType.function.apply(assetConfig);
        AssetConfigDO.OutInputFilter outInputFilter = outInput.getOutInputFunction.apply(blackOrWhiteList);
        return border.getHitableFunction.test(assetJson, outInputFilter, outInput.getIpFunction);

    }

    private static boolean overseasHit(JSONObject assetJson, AssetConfigDO.OutInputFilter outInputFilter,
                                       Function<JSONObject, Long> getIp) {
        AssetConfigDO.OverseasFilter overseas = outInputFilter.getOverseas();
        if (overseas == null) {
            return false;
        }
        return overseas.hit(getIp.apply(assetJson), GET_PROTOCOL.apply(assetJson),
                GET_PORT.apply(assetJson));
    }

    private static boolean domesticHit(JSONObject assetJson, AssetConfigDO.OutInputFilter outInputFilter,
                                       Function<JSONObject, Long> getIp) {
        List<AssetConfigDO.DomesticFilter> domestic = outInputFilter.getDomestic();
        if (CollectionUtils.isEmpty(domestic)) {
            return false;
        }
        return domestic.stream().anyMatch(domesticFilter ->
                domesticFilter.hit(getIp.apply(assetJson), GET_PROTOCOL.apply(assetJson),
                        GET_PORT.apply(assetJson)));

    }



    /**
     * ***********************************************************
     * ***********          enum                          ******
     * ***********************************************************
     **/


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

        public TePredicate<JSONObject, AssetConfigDO.OutInputFilter, Function<JSONObject, Long>> getHitableFunction;

        Border(TePredicate<JSONObject, AssetConfigDO.OutInputFilter, Function<JSONObject, Long>> getHitableFunction) {
            this.getHitableFunction = getHitableFunction;
        }
    }


}
