package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.assetextension.AssetIsakmpExtension;
import com.tincery.gaea.api.dm.assetextension.AssetSshExtension;
import com.tincery.gaea.api.dm.assetextension.AssetSslExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.dw.MergeAble;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AssetExtension extends SimpleBaseDO implements MergeAble<AssetExtension> {

    @Id
    private String id;
    private String assetUnit;
    private String assetName;

    private List<AssetSslExtension> sslExtensions;
    private List<AssetSshExtension> sshExtensions;
    private List<AssetIsakmpExtension> isakmpExtensions;

    public static AssetExtension fromAssetJsonObject(JSONObject jsonObject) {
        int assetFlag = jsonObject.getInteger(HeadConst.FIELD.ASSET_FLAG);
        AssetExtension assetExtension = new AssetExtension();
        assetExtension.setAssetUnit(jsonObject.getString("$assetUnit"));
        assetExtension.setAssetName(jsonObject.getString("$assetName"));
        assetExtension.setId();
        String proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        switch (proName) {
            case HeadConst.PRONAME.SSL:
                AssetSslExtension sslExtension = new AssetSslExtension();
                sslExtension.append(jsonObject);
                break;
            case HeadConst.PRONAME.SSH:
                break;
            case HeadConst.PRONAME.ISAKMP:
                AssetIsakmpExtension isakmpExtension = new AssetIsakmpExtension();
                isakmpExtension.append(jsonObject);
                break;
            default:
                break;
        }
        return assetExtension;
    }

    public void setId() {
        this.id = this.assetUnit + "_" + this.assetName;
    }

    @Override
    public AssetExtension merge(AssetExtension that) {
        Map<String, AssetSslExtension> assetSslExtensionMap = new HashMap<>();
        this.sslExtensions.forEach(ssl -> assetSslExtensionMap.merge(ssl.getId(), ssl, (k, v) -> (AssetSslExtension) v.merge(ssl)));
        that.sslExtensions.forEach(ssl -> assetSslExtensionMap.merge(ssl.getId(), ssl, (k, v) -> (AssetSslExtension) v.merge(ssl)));
        this.sslExtensions = new ArrayList<>(assetSslExtensionMap.values());
        return this;
    }

}
