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
    private String unit;
    private Long ip;
    private String name;
    private String proName;
    private String proTag;
    private Integer port;

    private List<AssetSslExtension> sslExtensions;
    private List<AssetOpenVpnExtension> openVpnExtensions;
    private List<AssetSshExtension> sshExtensions;
    private List<AssetIsakmpInitiatorExtension> isakmpInitiatorExtensions;
    private List<AssetIsakmpResponderExtension> isakmpResponderExtensions;
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
                assetExtension.setProTag(proName);
                break;
            case HeadConst.PRONAME.OPENVPN:
                AssetOpenVpnExtension openVpnExtension = new AssetOpenVpnExtension();
                if (!openVpnExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendOpenVpnExtension(openVpnExtension);
                assetExtension.setProTag(proName);
                break;
            case HeadConst.PRONAME.SSH:
                AssetSshExtension sshExtension = new AssetSshExtension();
                if (!sshExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendSshExtension(sshExtension);
                assetExtension.setProTag(proName);
                break;
            case HeadConst.PRONAME.ISAKMP:
                boolean isClient = jsonObject.getBoolean("isClient");
                if (isClient) {
                    AssetIsakmpInitiatorExtension isakmpInitiatorExtension = new AssetIsakmpInitiatorExtension();
                    if (!isakmpInitiatorExtension.create(jsonObject)) {
                        return null;
                    }
                    assetExtension.appendIsakmpInitiatorExtension(isakmpInitiatorExtension);
                    assetExtension.setProTag(proName + "Initiator");
                } else {
                    AssetIsakmpResponderExtension isakmpResponderExtension = new AssetIsakmpResponderExtension();
                    if (!isakmpResponderExtension.create(jsonObject)) {
                        return null;
                    }
                    assetExtension.appendIsakmpResponderExtension(isakmpResponderExtension);
                    assetExtension.setProTag(proName + "Responder");
                }
                break;
            case HeadConst.PRONAME.PPTP:
            case HeadConst.PRONAME.L2TP:
                AssetPptpAndL2tpExtension pptpAndL2tpExtension = new AssetPptpAndL2tpExtension();
                if (!pptpAndL2tpExtension.create(jsonObject)) {
                    return null;
                }
                assetExtension.appendPptpAndL2tpExtension(pptpAndL2tpExtension);
                assetExtension.setProTag(proName);
                break;
            default:
                return null;
        }
        assetExtension.setProName(proName);
        return assetExtension;
    }

    @Override
    public AssetExtension merge(AssetExtension that) {
        switch (this.proTag) {
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
            case HeadConst.PRONAME.ISAKMP + "Initiator":
                Map<String, AssetIsakmpInitiatorExtension> assetIsakmpInitiatorExtensionMap = new HashMap<>();
                this.isakmpInitiatorExtensions.forEach(isakmp -> assetIsakmpInitiatorExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpInitiatorExtension) v.merge(isakmp)));
                that.isakmpInitiatorExtensions.forEach(isakmp -> assetIsakmpInitiatorExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpInitiatorExtension) v.merge(isakmp)));
                this.isakmpInitiatorExtensions = new ArrayList<>(assetIsakmpInitiatorExtensionMap.values());
                break;
            case HeadConst.PRONAME.ISAKMP + "Responder":
                Map<String, AssetIsakmpResponderExtension> assetIsakmpResponderExtensionMap = new HashMap<>();
                this.isakmpResponderExtensions.forEach(isakmp -> assetIsakmpResponderExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpResponderExtension) v.merge(isakmp)));
                that.isakmpResponderExtensions.forEach(isakmp -> assetIsakmpResponderExtensionMap.merge(isakmp.getId(), isakmp, (k, v) -> (AssetIsakmpResponderExtension) v.merge(isakmp)));
                this.isakmpResponderExtensions = new ArrayList<>(assetIsakmpResponderExtensionMap.values());
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
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.ip = jsonObject.getLong("ip");
        if (isClient && !HeadConst.PRONAME.ISAKMP.equals(this.proName)) {
            return false;
        }
        //测试_测试(192.168.1.234)_TCP(作为服务端)_22__1606114800000
        this.unit = jsonObject.getString("unit");
        this.name = jsonObject.getString("name");
        int protocol = jsonObject.getInteger(HeadConst.FIELD.PROTOCOL);
        if (protocol == 6) {
            if (isClient) {
                this.proTag = "TCP(作为客户端)";
            } else {
                this.proTag = "TCP(作为服务端)";
            }
        } else if (protocol == 17) {
            if (isClient) {
                this.proTag = "UDP(作为客户端)";
            } else {
                this.proTag = "UDP(作为服务端)";
            }
        } else {
            return false;
        }
        this.port = jsonObject.getInteger(HeadConst.FIELD.SERVER_PORT);
        this.id = this.unit + "_" + this.name + "(" + this.ip + ")_" + this.proTag + "_" + this.port;
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

    private void appendIsakmpInitiatorExtension(AssetIsakmpInitiatorExtension isakmpInitiatorExtension) {
        if (null == this.isakmpInitiatorExtensions) {
            this.isakmpInitiatorExtensions = new ArrayList<>();
        }
        this.isakmpInitiatorExtensions.add(isakmpInitiatorExtension);
    }

    private void appendIsakmpResponderExtension(AssetIsakmpResponderExtension isakmpResponderExtension) {
        if (null == this.isakmpResponderExtensions) {
            this.isakmpResponderExtensions = new ArrayList<>();
        }
        this.isakmpResponderExtensions.add(isakmpResponderExtension);
    }

    private void appendPptpAndL2tpExtension(AssetPptpAndL2tpExtension pptpAndL2tpExtension) {
        if (null == this.pptpAndL2tpExtensions) {
            this.pptpAndL2tpExtensions = new ArrayList<>();
        }
        this.pptpAndL2tpExtensions.add(pptpAndL2tpExtension);
    }

}
