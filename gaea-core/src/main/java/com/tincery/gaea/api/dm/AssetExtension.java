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

    private List<AssetSslExtension> sslExtensions = new ArrayList<>();
    private List<AssetSshExtension> sshExtensions = new ArrayList<>();
    private List<AssetIsakmpExtension> isakmpExtensions = new ArrayList<>();

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
                sslExtension.create(jsonObject);
                assetExtension.appendSslExtension(sslExtension);
                break;
            case HeadConst.PRONAME.SSH:
                AssetSshExtension sshExtension = new AssetSshExtension();
                sshExtension.create(jsonObject);
                assetExtension.appendSshExtension(sshExtension);
                break;
            case HeadConst.PRONAME.ISAKMP:
                AssetIsakmpExtension isakmpExtension = new AssetIsakmpExtension();
                isakmpExtension.create(jsonObject);
                assetExtension.appendIsakmpExtension(isakmpExtension);
                break;
            default:
                break;
        }
        return assetExtension;
    }

    private void appendSslExtension(AssetSslExtension sslExtension) {
        this.sslExtensions.add(sslExtension);
    }

    private void appendSshExtension(AssetSshExtension sshExtension) {
        this.sshExtensions.add(sshExtension);
    }

    private void appendIsakmpExtension(AssetIsakmpExtension isakmpExtension) {
        this.isakmpExtensions.add(isakmpExtension);
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
        Map<String, AssetSshExtension> assetSshExtensionMap = new HashMap<>();
        this.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
        that.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
        this.sshExtensions = new ArrayList<>(assetSshExtensionMap.values());
        Map<String, AssetIsakmpExtension> assetIsakmpExtensionMap = new HashMap<>();
        this.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
        that.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
        this.isakmpExtensions = new ArrayList<>(assetIsakmpExtensionMap.values());
        return this;
    }

}
