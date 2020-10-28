package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.mgt.HeadConst;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * assetconfig的工具类
 **/
public class AssetConfigs {

    /****
     * 作为客户端是一条资产的时候调用此方法  此方法将返回方法所检测的所有告警素材
     *
     * @param assetJson 一条资产json
     * @param assetDetector 资产检测器
     * @return 产生的报警报告内容
     **/
    public static List<AlarmMaterialData> detectorClient(JSONObject assetJson, AssetDetector assetDetector) {
        List<AlarmMaterialData> result = new ArrayList<>();
        AssetConfigDO assetConfig = assetDetector.getAsset(assetJson.getLong(HeadConst.CSV.CLIENT_IP_N));
        AssetConfigDO.OutInputFilter out = assetConfig.getBlackList().getOut();
        if (true) {
            // 命中之后继续匹配


        } else {
            // 命中之后不继续匹配
        }

    }

    public static List<AlarmMaterialData> detectorServer(JSONObject assetJson, AssetDetector assetDetector) {
        return assetJson;
    }

    public static List<AlarmMaterialData> detectorClientAndServer(JSONObject assetJson, AssetDetector assetDetector) {
        return assetJson;
    }


    private static boolean checkListAndStop(JSONObject assetJson, AssetConfigDO.OutInputFilter filter,
                                            FilterType type) {

        filter.getDomestic().stream().anyMatch(domesticFilter -> {
            if (domesticFilter.isUnique()) {
                return assetJsonOUT_DOMESTIC
            }
        })

    }


    public enum ListType {
        BLACK(AssetConfigDO::getBlackList),
        WHITE(AssetConfigDO::getWhiteList);

        private Function<AssetConfigDO, AssetConfigDO.BlackOrWhiteList> function;

        ListType(Function<AssetConfigDO, AssetConfigDO.BlackOrWhiteList> function) {
            this.function = function;
        }

        public AssetConfigDO.BlackOrWhiteList getList(AssetConfigDO assetConfig) {
            return this.function.apply(assetConfig);
        }
    }

    public enum OutInput {
        OUT(AssetConfigDO.BlackOrWhiteList::getOut),
        IN(AssetConfigDO.BlackOrWhiteList::getIn);

        private Function<AssetConfigDO.BlackOrWhiteList, AssetConfigDO.OutInputFilter> function;


        OutInput(Function<AssetConfigDO.BlackOrWhiteList, AssetConfigDO.OutInputFilter> function) {
            this.function = function;
        }



        public AssetConfigDO.OutInputFilter getFilter(AssetConfigDO.BlackOrWhiteList blackOrWhiteList) {
            return this.function.apply(blackOrWhiteList);
        }
    }

    public enum Border {
        DOMESTIC(AssetConfigs::outDomestic),
        OVERSEAS(AssetConfigs::inDomestic);

        // 判断是否命中
        private BiPredicate<JSONObject, AssetConfigDO.OutInputFilter> biPredicate;

        // 告警素材是如何由规则和资产产生的
        private BiFunction<JSONObject, AssetConfigDO, AlarmMaterialData> createAlarmFunction;

        Border(BiPredicate<JSONObject, AssetConfigDO.OutInputFilter> biPredicate,
               BiFunction<JSONObject, AssetConfigDO, AlarmMaterialData> createAlarmFunction) {
            this.biPredicate = biPredicate;
            this.createAlarmFunction = createAlarmFunction;
        }

        public boolean
    }

    public enum FilterType {
        OUT_DOMESTIC(AssetConfigs::outDomestic),
        OUT_OVERSEAS(AssetConfigs::outOverseas),
        IN_DOMESTIC(AssetConfigs::inDomestic),
        IN_OVERSEAS(AssetConfigs::inOverseas);

        // 判断是否命中
        private BiPredicate<JSONObject, AssetConfigDO.OutInputFilter> biPredicate;

        // 告警素材是如何由规则和资产产生的
        private BiFunction<JSONObject, AssetConfigDO, AlarmMaterialData> createAlarmFunction;

        FilterType(BiPredicate<JSONObject, AssetConfigDO.OutInputFilter> biPredicate,
                   BiFunction<JSONObject, AssetConfigDO, AlarmMaterialData> createAlarmFunction) {
            this.biPredicate = biPredicate;
            this.createAlarmFunction = createAlarmFunction;
        }
    }

    public AlarmMaterialData hitAndStop(JSONObject assetJson, AssetConfigDO.OutInputFilter outInputFilter,
                                        FilterType mode) {
        if (mode.biPredicate.test(assetJson, outInputFilter)) {
            return mode.createAlarmFunction.apply(assetJson, outInputFilter)
        }
    }

    private static boolean outDomestic(JSONObject jsonObject, AssetConfigDO.OutInputFilter outInputFilter) {
        return outInputFilter.getDomestic().stream().anyMatch(domesticFilter -> {
            long ip = jsonObject.getLong(HeadConst.CSV.SERVER_IP_N);
            int protocol = jsonObject.getInteger(HeadConst.CSV.PROTOCOL);
            int port = jsonObject.getInteger(HeadConst.CSV.SERVER_PORT);
            return domesticFilter.hit(ip, protocol, port);
        });
    }

    private static boolean outOverseas(JSONObject jsonObject, AssetConfigDO.OutInputFilter outInputFilter) {
        AssetConfigDO.OverseasFilter overseas = outInputFilter.getOverseas();
        long ip = jsonObject.getLong(HeadConst.CSV.SERVER_IP_N);
        int protocol = jsonObject.getInteger(HeadConst.CSV.PROTOCOL);
        int port = jsonObject.getInteger(HeadConst.CSV.SERVER_PORT);
        return overseas.hit(ip, protocol, port);
    }

    private static boolean inDomestic(JSONObject jsonObject, AssetConfigDO.OutInputFilter outInputFilter) {
        return outInputFilter.getDomestic().stream().anyMatch(domesticFilter -> {
            long ip = jsonObject.getLong(HeadConst.CSV.CLIENT_IP_N);
            int protocol = jsonObject.getInteger(HeadConst.CSV.PROTOCOL);
            int port = jsonObject.getInteger(HeadConst.CSV.CLIENT_PORT);
            return domesticFilter.hit(ip, protocol, port);
        });
    }

    private static boolean inOverseas(JSONObject jsonObject, AssetConfigDO.OutInputFilter outInputFilter) {
        AssetConfigDO.OverseasFilter overseas = outInputFilter.getOverseas();
        long ip = jsonObject.getLong(HeadConst.CSV.CLIENT_IP_N);
        int protocol = jsonObject.getInteger(HeadConst.CSV.PROTOCOL);
        int port = jsonObject.getInteger(HeadConst.CSV.CLIENT_PORT);
        return overseas.hit(ip, protocol, port);
    }


}
