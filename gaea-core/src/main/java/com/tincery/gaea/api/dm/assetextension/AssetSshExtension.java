package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import lombok.Data;

@Data
public class AssetSshExtension extends BaseAssetExtension {

    private String protocol;
    private String version;
    private String handshake;


    @Override
    public String getId() {
        return null;
    }

    @Override
    public void create(JSONObject jsonObject) {
        JSONObject sshExtension = jsonObject.getJSONObject(HeadConst.FIELD.SSH_EXTENSION);
        System.out.println(1);
    }

    @Override
    public void setKey() {

    }
}
