package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.OpenVpnExtension;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Insomnia
 */
@Setter
@Getter
public class OpenVpnData extends AbstractSrcData {

    private OpenVpnExtension openVpnExtension;

    @Override
    public String toCsv(char splitChar) {
        String extensionElements = null;
        String extension = null;
        if (null != this.openVpnExtension) {
            extensionElements = this.openVpnExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.openVpnExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.completeSession,
                this.malformedUpPayload, this.malformedDownPayload,
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    @Override
    public void adjust() {
        super.adjust();
        adjustDaulAuth();
        adjustSha1();
    }

    protected void adjustDaulAuth() {
        if (null == this.openVpnExtension.getClientCerChain() && null == this.openVpnExtension.getServerCerChain()) {
            return;
        }
        if (null != this.openVpnExtension.getClientCerChain() && null != this.openVpnExtension.getServerCerChain()) {
            this.completeSession = true;
            return;
        }
        this.completeSession = false;
    }

    protected void adjustSha1() {
        if (null == this.openVpnExtension.getServerCerChain()) {
            return;
        }
        this.openVpnExtension.setSha1(this.openVpnExtension.getServerCerChain().get(0).split("_")[0]);
    }

    public String getKey() {
        return this.protocol + "_" + this.clientIp + "_" + this.serverIp + "_" + this.clientPort + "_" + this.serverPort;
    }

    public void merge(OpenVpnData openVpnData) {
        this.capTime = Math.min(this.capTime, openVpnData.capTime);
        this.duration = Math.max(this.capTime + this.duration, openVpnData.capTime + openVpnData.duration);
        if (null == this.openVpnExtension) {
            this.openVpnExtension = openVpnData.getOpenVpnExtension();
        } else {
            this.openVpnExtension.merge(openVpnData.getOpenVpnExtension());
        }
    }

}
