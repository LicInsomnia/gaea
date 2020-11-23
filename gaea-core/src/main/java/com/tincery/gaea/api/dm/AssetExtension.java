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
    private String proName;
    private Integer port;
    private String dataSource;

    private List<AssetSslExtension> sslExtensions;
    private List<AssetOpenVpnExtension> openVpnExtensions;
    private List<AssetSshExtension> sshExtensions;
    private List<AssetIsakmpExtension> isakmpExtensions;
    private List<AssetPptpAndL2tpExtension> pptpAndL2tpExtensions;

    public static AssetExtension fromAssetJsonObject(JSONObject jsonObject) {
        AssetExtension assetExtension = new AssetExtension();
        if (!assetExtension.appendCommonInformation(jsonObject)) {
            return null;
        }
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
                return null;
        }
        assetExtension.setDataSource(proName);
        return assetExtension;
    }

    @Override
    public AssetExtension merge(AssetExtension that) {
        switch (this.dataSource) {
            case HeadConst.PRONAME.SSL:
                Map<String, AssetSslExtension> assetSslExtensionMap = new HashMap<>();
                this.sslExtensions.forEach(ssl -> assetSslExtensionMap.merge(ssl.getId(), ssl, (k, v) -> (AssetSslExtension) v.merge(ssl)));
                that.sslExtensions.forEach(ssl -> assetSslExtensionMap.merge(ssl.getId(), ssl, (k, v) -> (AssetSslExtension) v.merge(ssl)));
                this.sslExtensions = new ArrayList<>(assetSslExtensionMap.values());
                break;
            case HeadConst.PRONAME.OPENVPN:
                Map<String, AssetOpenVpnExtension> assetOpenVpnExtensionMap = new HashMap<>();
                this.openVpnExtensions.forEach(openVpn -> assetOpenVpnExtensionMap.merge(openVpn.getId(), openVpn, (k, v) -> (AssetOpenVpnExtension) v.merge(openVpn)));
                that.openVpnExtensions.forEach(openVpn -> assetOpenVpnExtensionMap.merge(openVpn.getId(), openVpn, (k, v) -> (AssetOpenVpnExtension) v.merge(openVpn)));
                this.openVpnExtensions = new ArrayList<>(assetOpenVpnExtensionMap.values());
                break;
            case HeadConst.PRONAME.SSH:
                Map<String, AssetSshExtension> assetSshExtensionMap = new HashMap<>();
                this.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
                that.sshExtensions.forEach(ssh -> assetSshExtensionMap.merge(ssh.getId(), ssh, (k, v) -> (AssetSshExtension) v.merge(ssh)));
                this.sshExtensions = new ArrayList<>(assetSshExtensionMap.values());
                break;
            case HeadConst.PRONAME.ISAKMP:
                Map<String, AssetIsakmpExtension> assetIsakmpExtensionMap = new HashMap<>();
                this.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
                that.isakmpExtensions.forEach(isakmp -> assetIsakmpExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpExtension) v.merge(isakmp)));
                this.isakmpExtensions = new ArrayList<>(assetIsakmpExtensionMap.values());
                break;
            case HeadConst.PRONAME.PPTP:
            case HeadConst.PRONAME.L2TP:
                Map<String, AssetPptpAndL2tpExtension> assetPptpAndL2tpExtensionMap = new HashMap<>();
                this.pptpAndL2tpExtensions.forEach(pptpAndL2tp -> assetPptpAndL2tpExtensionMap.merge(pptpAndL2tp.getId(), pptpAndL2tp, (k, v) -> (AssetPptpAndL2tpExtension) v.merge(pptpAndL2tp)));
                that.pptpAndL2tpExtensions.forEach(pptpAndL2tp -> assetPptpAndL2tpExtensionMap.merge(pptpAndL2tp.getId(), pptpAndL2tp, (k, v) -> (AssetPptpAndL2tpExtension) v.merge(pptpAndL2tp)));
                this.pptpAndL2tpExtensions = new ArrayList<>(assetPptpAndL2tpExtensionMap.values());
                break;
            default:
                break;
        }
        return this;
    }

    private boolean appendCommonInformation(JSONObject jsonObject) {
        boolean isClient = jsonObject.getBoolean("isClient");
        String proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        if (isClient && !HeadConst.PRONAME.ISAKMP.equals(proName)) {
            return false;
        }
        //测试_测试(192.168.1.234)_TCP(作为服务端)_22__1606114800000
        this.assetUnit = jsonObject.getString("unit");
        this.assetName = jsonObject.getString("name");
        int protocol = jsonObject.getInteger(HeadConst.FIELD.PROTOCOL);
        if (protocol == 6) {
            if (isClient) {
                this.proName = "TCP(作为客户端)";
            } else {
                this.proName = "TCP(作为服务端)";
            }
        } else if (protocol == 17) {
            if (isClient) {
                this.proName = "UDP(作为客户端)";
            } else {
                this.proName = "UDP(作为服务端)";
            }
        } else {
            return false;
        }
        this.port = jsonObject.getInteger(HeadConst.FIELD.SERVER_PORT);
        this.id = this.assetUnit + "_" + this.assetName + "_" + this.proName + "_" + this.port;
        return true;
    }

    private void appendSslExtension(AssetSslExtension sslExtension) {
        if (null == this.sslExtensions) {
            this.sslExtensions = new ArrayList<>();
        }
        this.sslExtensions.add(sslExtension);
    }

    private void appendOpenVpnExtension(AssetOpenVpnExtension openVpnExtension) {
        if (null == this.openVpnExtensions) {
            this.openVpnExtensions = new ArrayList<>();
        }
        this.openVpnExtensions.add(openVpnExtension);
    }

    private void appendSshExtension(AssetSshExtension sshExtension) {
        if (null == this.sshExtensions) {
            this.sshExtensions = new ArrayList<>();
        }
        this.sshExtensions.add(sshExtension);
    }

    private void appendIsakmpExtension(AssetIsakmpExtension isakmpExtension) {
        if (null == this.isakmpExtensions) {
            this.isakmpExtensions = new ArrayList<>();
        }
        this.isakmpExtensions.add(isakmpExtension);
    }

    private void appendPptpAndL2tpExtension(AssetPptpAndL2tpExtension pptpAndL2tpExtension) {
        if (null == this.pptpAndL2tpExtensions) {
            this.pptpAndL2tpExtensions = new ArrayList<>();
        }
        this.pptpAndL2tpExtensions.add(pptpAndL2tpExtension);
    }

}
