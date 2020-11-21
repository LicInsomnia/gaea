package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

@Data
public class AssetIsakmpExtension implements MergeAble<AssetIsakmpExtension> {

    private String protocolKey;

    public void append(JSONObject jsonObject) {
        JSONObject isakmpExtension = jsonObject.getJSONObject(HeadConst.FIELD.IASKMP_EXTENSION);
        this.protocolKey = jsonObject.getString(HeadConst.FIELD.PRONAME);
        System.out.println(1);
    }

    @Override
    public AssetIsakmpExtension merge(AssetIsakmpExtension that) {
        return this;
    }

    @Override
    public String getId() {
        return null;
    }
}
