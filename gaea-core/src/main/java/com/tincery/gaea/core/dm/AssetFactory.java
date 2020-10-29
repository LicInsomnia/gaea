/*
package com.tincery.gaea.core.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.AssetDataDTO;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;

import java.util.List;

*/
/**
 * @author gxz gongxuanzhang@foxmail.com
 **//*

public class AssetFactory {


    public static AssetDataDTO mergeIpAsset(List<JSONObject> assetJsonObjects){
        AssetDataDTO ipAsset = new AssetDataDTO();
        JSONObject first = assetJsonObjects.get(0);
        String id =

    }
    public static AssetDataDTO mergePortAsset(List<JSONObject> assetJsonObjects){

    }
    public static AssetDataDTO mergeProtocolAsset(List<JSONObject> assetJsonObjects){

    }
    public static AssetDataDTO mergeUnitAsset(List<JSONObject> assetJsonObjects){

    }

    */
/****
 * 将一个资产json 的时间取整
 **//*

    private static long clearCaptime(JSONObject jsonObject){
        Long capTime = jsonObject.getLong(HeadConst.CSV.CAPTIME);
        // 分钟取整
        return capTime / DateUtils.MINUTE * DateUtils.MINUTE;
    }
}
*/
