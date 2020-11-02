package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ProtocolType;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class AssetGroupSupport {


    /****
     * 把jsonList 解析成最终直接插入数据的内容
     * @author gxz
     **/
    public static List<AssetDataDTO> getSaveDataByAll(List<JSONObject> allData,
                                                      Function<JSONObject, AssetDataDTO> map) {
        Map<String, List<AssetDataDTO>> allGroup =
                allData.stream().map(map).collect(Collectors.groupingBy(AssetDataDTO::getKey));
        return mergeAllData(allGroup);
    }

    public static List<AssetDataDTO> getSaveDataByServerAndClient(List<JSONObject> clientData, Function<JSONObject,
            AssetDataDTO> clientMap, List<JSONObject> serverData, Function<JSONObject,
            AssetDataDTO> serverMap) {
        Map<String, List<AssetDataDTO>> clientGroup =
                clientData.stream().map(clientMap).collect(Collectors.groupingBy(AssetDataDTO::getKey));
        Map<String, List<AssetDataDTO>> serverGroup =
                serverData.stream().map(serverMap).collect(Collectors.groupingBy(AssetDataDTO::getKey));
        return mergeClientServer(clientGroup, serverGroup);
    }


    /**
     * ***************************************
     * 从json映射成Asset的不同策略           ****
     * ***************************************
     **/


    public static AssetDataDTO unitDataFrom(JSONObject jsonObject) {
        AssetDataDTO assetDataDTO = jsonObject.toJavaObject(AssetDataDTO.class);
        long capTime = jsonObject.getLongValue("capTime");
        capTime = capTime / DateUtils.DAY * DateUtils.DAY;
        LocalDateTime timeTag = LocalDateTime.ofInstant(Instant.ofEpochMilli(capTime), ZoneId.systemDefault());
        assetDataDTO.setId(assetDataDTO.getUnit() + "_" + capTime);
        assetDataDTO.setTimeTag(timeTag);
        assetDataDTO.setSessionCount(1L);
        assetDataDTO.setKey(assetDataDTO.getId());
        return assetDataDTO;
    }


    public static AssetDataDTO clientIpDataFrom(JSONObject jsonObject) {
        return ipDataFrom(jsonObject, getClientIp());
    }

    public static AssetDataDTO serverIpDataFrom(JSONObject jsonObject) {
        return ipDataFrom(jsonObject, getServerIp());
    }

    public static AssetDataDTO ipDataFrom(JSONObject jsonObject, Function<JSONObject, String> getIpFunction) {
        AssetDataDTO assetDataDTO = jsonObject.toJavaObject(AssetDataDTO.class);
        String ip = getIpFunction.apply(jsonObject);
        String name = StringUtils.fillString("{}({})", assetDataDTO.getName(), ip);
        long capTime = jsonObject.getLongValue("capTime") / DateUtils.DAY * DateUtils.DAY;
        LocalDateTime timeTag = LocalDateTime.ofInstant(Instant.ofEpochMilli(capTime), ZoneId.systemDefault());
        String id = assetDataDTO.getUnit() + "_" + name + capTime;
        assetDataDTO.setKey(id).setId(id).setIp(ip);
        assetDataDTO.setTimeTag(timeTag);
        assetDataDTO.setSessionCount(1L);
        assetDataDTO.setName(name);
        return assetDataDTO;
    }

    public static AssetDataDTO serverProtocolDataFrom(JSONObject jsonObject) {
        AssetDataDTO assetDataDTO = jsonObject.toJavaObject(AssetDataDTO.class);
        assetDataDTO.setIp(getServerIp().apply(jsonObject));
        assetDataDTO.setProname(formatProtocol(jsonObject.getIntValue("protocol")) + "(作为服务端)");
        String name = StringUtils.fillString("{}({})", assetDataDTO.getName(), assetDataDTO.getIp());
        assetDataDTO.setName(name);
        return getProtocolDataForm(assetDataDTO, jsonObject);
    }

    public static AssetDataDTO clientProtocolDataFrom(JSONObject jsonObject) {
        AssetDataDTO assetDataDTO = jsonObject.toJavaObject(AssetDataDTO.class);
        assetDataDTO.setIp(getClientIp().apply(jsonObject));
        assetDataDTO.setProname(formatProtocol(jsonObject.getIntValue("protocol")) + "(作为客户端)");
        String name = StringUtils.fillString("{}({})", assetDataDTO.getName(), assetDataDTO.getIp());
        assetDataDTO.setName(name);
        return getProtocolDataForm(assetDataDTO, jsonObject);
    }


    public static AssetDataDTO portDataFrom(JSONObject jsonObject) {
        AssetDataDTO assetDataDTO = serverProtocolDataFrom(jsonObject);
        String key = assetDataDTO.getKey();
        int i = key.lastIndexOf("_");
        key = key.substring(0, i) + "_" + jsonObject.getIntValue(HeadConst.FIELD.SERVER_PORT) + "_" + key.substring(i);
        assetDataDTO.setKey(key);
        assetDataDTO.setClients(serverIpGetClient(jsonObject));
        return assetDataDTO.setId(null);
    }


    /**
     * **************************
     * 分组合并的不同策略        **
     * **************************
     **/

    public static List<AssetDataDTO> mergeAllData(Map<String, List<AssetDataDTO>> groupMap) {
        List<AssetDataDTO> result = new ArrayList<>(groupMap.size());
        groupMap.forEach((key, list) -> {
            AssetDataDTO reduce = list.stream().reduce(AssetDataDTO::merge).get();
            reduce.setSessionCount((long) list.size());
            result.add(reduce.adjust());
        });
        return result;
    }

    public static List<AssetDataDTO> mergeClientServer(Map<String, List<AssetDataDTO>> clientGroup, Map<String,
            List<AssetDataDTO>> serverGroup) {
        List<AssetDataDTO> result = new ArrayList<>(clientGroup.size() + serverGroup.size());
        serverGroup.forEach((key, serverList) -> clientGroup.merge(key, serverList, (x, y) -> {
            x.addAll(y);
            return x;
        }));
        clientGroup.forEach((key, allList) -> {
            AssetDataDTO reduce = allList.stream().reduce(AssetDataDTO::merge).get();
            result.add(reduce);
        });
        return result;
    }


    /**
     * ******************************************
     * support                                  *
     * ******************************************
     **/


    private static Function<JSONObject, String> getClientIp() {
        return (json) -> json.getString(HeadConst.FIELD.CLIENT_IP);
    }

    private static Function<JSONObject, String> getServerIp() {
        return (json) -> json.getString(HeadConst.FIELD.SERVER_IP);
    }

    private static String formatProtocol(int value) {
        for (ProtocolType protocolType : ProtocolType.values()) {
            if (protocolType.getValue() == value) {
                return protocolType.toString();
            }
        }
        throw new IllegalArgumentException();
    }

    private static AssetDataDTO getProtocolDataForm(AssetDataDTO assetDataDTO, JSONObject jsonObject) {
        long capTime = jsonObject.getLongValue("capTime") / DateUtils.HOUR * DateUtils.HOUR;
        assetDataDTO.setTimeTag(LocalDateTime.ofInstant(Instant.ofEpochMilli(capTime), ZoneOffset.systemDefault()));
        String key = StringUtils.fillString("{}_{}_{}_{}", assetDataDTO.getUnit(), assetDataDTO.getName(),
                assetDataDTO.getProname(), capTime);
        return assetDataDTO.setKey(key).setId(null);
    }

    /****
     * 针对作为服务端的资产    将客户端信息封装成client内容
     **/
    private static List<AssetDataDTO.AssetClient> serverIpGetClient(JSONObject jsonObject) {
        List<AssetDataDTO.AssetClient> clients = new ArrayList<>();
        String clientIp = jsonObject.getString(HeadConst.FIELD.CLIENT_IP);
        AssetDataDTO.AssetClient client = new AssetDataDTO.AssetClient();
        JSONObject location = jsonObject.getJSONObject(HeadConst.FIELD.CLIENT_LOCATION);
        if (location == null) {
            client.setCountry("-").setForeign(false);
        } else {
            String country = location.getString("country");
            client.setForeign(!"China".equals(country));
        }
        client.setClientIp(clientIp).setValue(1L);
        clients.add(client);
        return clients;
    }

    /****
     * 复查  如果数据库中有相同ID的信息 整合更新
     * @param assetUnitDao 相应的dao层实体
     * @param list  已经计算好的数据
     **/
    public static void rechecking(SimpleBaseDaoImpl<AssetDataDTO> assetUnitDao, List<AssetDataDTO> list) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(list.stream().map(AssetDataDTO::getId).collect(Collectors.toList())));
        List<AssetDataDTO> mongoData = assetUnitDao.findListData(query);
        if (!CollectionUtils.isEmpty(mongoData)) {
            list.forEach(asset -> {
                for (AssetDataDTO mongoUnitData : mongoData) {
                    if (mongoUnitData.getId().equals(asset.getId())) {
                        asset.merge(mongoUnitData);
                        break;
                    }
                }
            });
        }
    }

}