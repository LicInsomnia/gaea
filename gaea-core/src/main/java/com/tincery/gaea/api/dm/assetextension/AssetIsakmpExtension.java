package com.tincery.gaea.api.dm.assetextension;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

@Data
public class AssetIsakmpExtension implements MergeAble<AssetIsakmpExtension> {

    private String protocol;
    private String version;
    private String handshake;


    @Override
    public AssetIsakmpExtension merge(AssetIsakmpExtension assetSslExtension) {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
