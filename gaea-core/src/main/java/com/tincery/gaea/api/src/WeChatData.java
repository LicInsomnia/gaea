package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 *
 */
@Setter
@Getter
public class WeChatData extends AbstractSrcData {

    /**
     * 微信私有属性
     */
    private String wxNum;
    private String version;
    private String osType;


    @Override
    public void adjust() {
        super.adjustUserId();
        super.adjustServerId();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.wxNum,this.version,this.osType
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    public JSONObject toJsonObjects() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WeChatData that = (WeChatData) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(capTime, that.capTime) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(proName, that.proName) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(clientMac, that.clientMac) &&
                Objects.equals(serverMac, that.serverMac) &&
                Objects.equals(clientIp, that.clientIp) &&
                Objects.equals(serverIp, that.serverIp) &&
                Objects.equals(clientPort, that.clientPort) &&
                Objects.equals(serverPort, that.serverPort) &&
                Objects.equals(clientIpOuter, that.clientIpOuter) &&
                Objects.equals(serverIpOuter, that.serverIpOuter) &&
                Objects.equals(clientPortOuter, that.clientPortOuter) &&
                Objects.equals(serverPortOuter, that.serverPortOuter) &&
                Objects.equals(protocolOuter, that.protocolOuter) &&
                Objects.equals(imp, that.imp) &&
                Objects.equals(malformedUpPayload, that.malformedUpPayload) &&
                Objects.equals(malformedDownPayload, that.malformedDownPayload) &&
                Objects.equals(imsi, that.imsi) &&
                Objects.equals(imei, that.imei) &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(groupName, that.groupName) &&
                Objects.equals(targetName, that.targetName) &&
                Objects.equals(upPkt, that.upPkt) &&
                Objects.equals(upByte, that.upByte) &&
                Objects.equals(downPkt, that.downPkt) &&
                Objects.equals(downByte, that.downByte) &&
                Objects.equals(duration, that.duration) &&
                Objects.equals(syn, that.syn) &&
                Objects.equals(fin, that.fin) &&
                Objects.equals(foreign, that.foreign) &&
                Objects.equals(eventData, that.eventData) &&
                Objects.equals(caseTags, that.caseTags) &&
                Objects.equals(caseTags, that.caseTags) &&
                Objects.equals(macOuter, that.macOuter) &&
                Objects.equals(completeSession, that.completeSession) &&
                Objects.equals(wxNum, that.wxNum) &&
                Objects.equals(version, that.version) &&
                Objects.equals(osType, that.osType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),wxNum,version,osType, caseTags,macOuter,completeSession,source, capTime, userId, serverId, proName, protocol, clientMac, serverMac, clientIp, serverIp, clientPort, serverPort, clientIpOuter, serverIpOuter, clientPortOuter, serverPortOuter, protocolOuter, imp, malformedUpPayload, malformedDownPayload, imsi, imei, msisdn, dataType, groupName, targetName, upPkt, upByte, downPkt, downByte, duration, syn, fin, foreign, eventData, caseTags);
    }

}
