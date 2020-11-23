package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.assetextension.*;
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
    private List<AssetOpenVpnExtension> openVpnExtensions = new ArrayList<>();
    private List<AssetSshExtension> sshExtensions = new ArrayList<>();
    private List<AssetIsakmpExtension> isakmpExtensions = new ArrayList<>();
    private List<AssetPptpAndL2tpExtension> pptpAndL2tpExtensions = new ArrayList<>();

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
                if (!sslExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendSslExtension(sslExtension);
                break;
            case HeadConst.PRONAME.OPENVPN:
                AssetOpenVpnExtension openVpnExtension = new AssetOpenVpnExtension();
                if (!openVpnExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendOpenVpnExtension(openVpnExtension);
                break;
            case HeadConst.PRONAME.SSH:
                AssetSshExtension sshExtension = new AssetSshExtension();
                if (!sshExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendSshExtension(sshExtension);
                break;
            case HeadConst.PRONAME.ISAKMP:
                AssetIsakmpExtension isakmpExtension = new AssetIsakmpExtension();
                if (!isakmpExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendIsakmpExtension(isakmpExtension);
                break;
            case HeadConst.PRONAME.PPTP:
            case HeadConst.PRONAME.L2TP:
                AssetPptpAndL2tpExtension pptpAndL2tpExtension = new AssetPptpAndL2tpExtension();
                if (!pptpAndL2tpExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendPptpAndL2tpExtension(pptpAndL2tpExtension);
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
        Map<String, AssetOpenVpnExtension> assetOpenVpnExtensionMap = new HashMap<>();
        this.openVpnExtensions.forEach(openVpn -> assetOpenVpnExtensionMap.merge(openVpn.getId(), openVpn, (k, v) -> (AssetOpenVpnExtension) v.merge(openVpn)));
        that.openVpnExtensions.forEach(openVpn -> assetOpenVpnExtensionMap.merge(openVpn.getId(), openVpn, (k, v) -> (AssetOpenVpnExtension) v.merge(openVpn)));
        this.openVpnExtensions = new ArrayList<>(assetOpenVpnExtensionMap.values());
        Map<String, AssetSshExtension> assetSshExtensionMap = new HashMap<>();
        this.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
        that.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
        this.sshExtensions = new ArrayList<>(assetSshExtensionMap.values());
        Map<String, AssetIsakmpExtension> assetIsakmpExtensionMap = new HashMap<>();
        this.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
        that.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
        this.isakmpExtensions = new ArrayList<>(assetIsakmpExtensionMap.values());
        Map<String, AssetPptpAndL2tpExtension> assetPptpAndL2tpExtensionMap = new HashMap<>();
        this.pptpAndL2tpExtensions.forEach(pptpAndL2tp -> assetPptpAndL2tpExtensionMap.merge(pptpAndL2tp.getId(), pptpAndL2tp, (k, v) -> (AssetPptpAndL2tpExtension) v.merge(pptpAndL2tp)));
        that.pptpAndL2tpExtensions.forEach(pptpAndL2tp -> assetPptpAndL2tpExtensionMap.merge(pptpAndL2tp.getId(), pptpAndL2tp, (k, v) -> (AssetPptpAndL2tpExtension) v.merge(pptpAndL2tp)));
        this.pptpAndL2tpExtensions = new ArrayList<>(assetPptpAndL2tpExtensionMap.values());
        return this;
    }

    private void appendSslExtension(AssetSslExtension sslExtension) {
        this.sslExtensions.add(sslExtension);
    }

    private void appendOpenVpnExtension(AssetOpenVpnExtension openVpnExtension) {
        this.openVpnExtensions.add(openVpnExtension);
    }

    private void appendSshExtension(AssetSshExtension sshExtension) {
        this.sshExtensions.add(sshExtension);
    }

    private void appendIsakmpExtension(AssetIsakmpExtension isakmpExtension) {
        this.isakmpExtensions.add(isakmpExtension);
    }

    private void appendPptpAndL2tpExtension(AssetPptpAndL2tpExtension pptpAndL2tpExtension) {
        this.pptpAndL2tpExtensions.add(pptpAndL2tpExtension);
    }

}
