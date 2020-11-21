package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

@Data
public class AssetIsakmpExtension implements MergeAble<AssetIsakmpExtension> {

    public void append(JSONObject jsonObject) {

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
