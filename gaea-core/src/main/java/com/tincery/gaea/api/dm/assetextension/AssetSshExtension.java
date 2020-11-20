package com.tincery.gaea.api.dm.assetextension;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

@Data
public class AssetSshExtension implements MergeAble<AssetSshExtension> {

    private String protocol;
    private String version;
    private String handshake;


    @Override
    public AssetSshExtension merge(AssetSshExtension assetSslExtension) {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
