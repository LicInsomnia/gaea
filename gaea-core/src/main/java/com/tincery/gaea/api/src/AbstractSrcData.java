package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 密数据元数据类
 *
 * @author Insomnia
 * @version 1.0.1
 * @date 2018/12/29
 */
@Getter
@Setter
public abstract class AbstractSrcData extends AbstractMetaData {

    /**
     * 标签
     */
    protected Set<String> caseTags;
    /**
     * 是否将特殊会话的MAC地址字段转为外层五元组
     */
    protected Boolean macOuter;
    /**
     * 会话是否完整
     */
    protected Boolean completeSession;

    @Override
    public void adjust() {
        adjustPayload();
        adjustUserId();
        adjustServerId();
        setOuterFromMac();
        adjustEventData();
        adjustCompleteSession();
    }

    protected void adjustEventData() {
        this.eventData = ToolUtils.getMD5(this.toString());
    }

    @Override
    public String toCsv(char splitChar) {
        String serverIpN = this.serverIp == null ? "" : ToolUtils.IP2long(this.serverIp) + "";
        String clientIpN = this.serverIp == null ? "" : ToolUtils.IP2long(this.clientIp) + "";
        Object[] join = new Object[]{
                this.groupName, this.targetName, this.userId, this.serverId, super.toCsv(splitChar),
                this.clientMac, this.serverMac, this.protocol, this.proName, this.clientIp, clientIpN,
                this.serverIp, serverIpN, this.clientPort, this.serverPort, this.clientIpOuter,
                this.serverIpOuter, this.clientPortOuter, this.serverPortOuter, this.protocolOuter, this.upPkt,
                this.upByte, this.downPkt, this.downByte, this.dataType, this.imsi, this.imei, this.msisdn,
                SourceFieldUtils.formatCollection(caseTags), this.foreign, this.duration, this.syn, this.fin};
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    protected void adjustPayload() {
        final String p = "0000000000000000000000000000000000000000";
        if (p.equals(this.malformedDownPayload)) {
            this.malformedUpPayload = null;
        }
        if (p.equals(this.malformedUpPayload)) {
            this.malformedUpPayload = null;
        }
    }

    private void addCaseTag(String tag) {
        Set<String> caseTags = getCaseTags();
        if (caseTags == null) {
            caseTags = new HashSet<>();
            setCaseTags(caseTags);
        }
        caseTags.add(tag);
    }

    protected void setOuterFromMac() {
        if (null == this.macOuter || !this.macOuter) {
            return;
        }
        this.macOuter = true;
        this.addCaseTag("解密");
        String[] clientOuter = this.getClientMac().split(":");
        int a = Integer.parseInt(clientOuter[3], 16);
        int b = Integer.parseInt(clientOuter[2], 16);
        int c = Integer.parseInt(clientOuter[1], 16);
        int d = Integer.parseInt(clientOuter[0], 16);
        int p = Integer.parseInt(clientOuter[5] + clientOuter[4], 16);
        this.setClientIpOuter(Joiner.on(".").join(new Object[]{a, b, c, d}));
        this.setClientPortOuter(p);
        String[] serverOuter = this.getServerMac().split(":");
        a = Integer.parseInt(serverOuter[3], 16);
        b = Integer.parseInt(serverOuter[2], 16);
        c = Integer.parseInt(serverOuter[1], 16);
        d = Integer.parseInt(serverOuter[0], 16);
        p = Integer.parseInt(serverOuter[5] + serverOuter[4], 16);
        this.setServerIpOuter(Joiner.on(".").join(new Object[]{a, b, c, d}));
        this.setServerPortOuter(p);
    }

    protected void adjustUserId() {
        if (StringUtils.isNotEmpty(this.imsi)) {
            this.userId = this.imsi;
        } else if (StringUtils.isEmpty(this.userId)) {
            this.userId = this.clientIp;
        }
    }

    protected void adjustServerId() {
        if (StringUtils.isEmpty(this.serverId)) {
            this.serverId = this.serverIp;
        }
    }

    protected void adjustCompleteSession() {
        if (null != this.syn && this.syn && null != fin && this.fin) {
            this.completeSession = true;
        }
    }

}
