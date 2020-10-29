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
public class OpenVpnData extends SslData {

    private OpenVpnExtension openVpnExtension;

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.duration, this.syn, this.fin,
                this.malformedUpPayload, this.malformedDownPayload,
                this.openVpnExtension.toCsv(splitChar),
                JSONObject.toJSONString(this.openVpnExtension)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    public String getKey() {
        return this.protocol + "_" + this.clientIp + "_" + this.serverIp + "_" + this.clientPort + "_" + this.serverPort;
    }

    public void merge(OpenVpnData openVpnData) {
        this.capTime = Math.min(this.capTime, openVpnData.capTime);
        this.duration = Math.max(this.capTime + this.duration, openVpnData.capTime + openVpnData.duration);
        this.openVpnExtension.merge(openVpnExtension);
    }

}
