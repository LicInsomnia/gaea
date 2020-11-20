package com.tincery.gaea.api.dm.assetextension;

import lombok.Data;

import java.util.List;

@Data
public class AssetSslExtension extends BaseAssetExtension {

    private String protocol;
    private String version;
    private String handshake;
    private List<String> aaa;


    @Override
    public BaseAssetExtension merge(BaseAssetExtension that) {
        super.merge(that);
        this.aaa.addAll(((AssetSslExtension) that).aaa);
        return this;
    }

}
