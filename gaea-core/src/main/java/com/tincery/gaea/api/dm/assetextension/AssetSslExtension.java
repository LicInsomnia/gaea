package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONPObject;
import lombok.Data;

@Data
public class AssetSslExtension extends BaseAssetExtension {

    /**
     * 表格行中字段，用于去重
     */
    private String protocolDescription;
    private String versionDescription;
    private String handshakeDescription;
    private String cipherSuiteDescription;
    private String cerChainDescription;
    private JSONPObject handshake;


    @Override
    public BaseAssetExtension merge(BaseAssetExtension that) {
        super.merge(that);
        return this;
    }

}
