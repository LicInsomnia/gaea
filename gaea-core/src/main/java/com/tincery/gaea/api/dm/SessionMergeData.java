package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class SessionMergeData extends AbstractDataWarehouseData {

    /**
     * 应用类型标识
     * 1.特殊应用（{"label.appType" : "specail"}）
     * 2.重点关注应用（{"label.appType" : "important")
     * 3.正常应用（{"label.appType" : "general"}）
     * 4.未知应用（{"label.appType" : "unknown"}）
     * 5.其它应用（{"label.appType" : "other"}）
     */
    protected String applicationType;
    /**
     * 应用识别可信度排序
     * 1.载荷(参与上下文识别迭代)
     * 2.挖掘(参与上下文识别迭代)
     * 3.dpi中ignore为false（参与上下文识别迭代）
     * 4.针对http新增的特殊字段检测(参与上下文识别迭代)
     * 5.静态特征(参与上下文识别迭代)
     * 6.keyword直接转义(参与上下文迭代)
     * 7.证书(参与上下文识别迭代)
     * 8.上下文
     * 9.serverIp2Application
     * 10.dns请求
     * 11.dpi中ignore为true
     * 12.protocol
     */
    protected ApplicationInformationBO application;
    protected Map<String, ApplicationInformationBO> applicationElements;
    protected String checkMode;


    protected JSONObject extension;

    public SessionMergeData(AbstractDataWarehouseData data) {
        this.source = data.getSource();
        this.capTime = data.getCapTime();
        this.userId = data.getUserId();
        this.serverId = data.getServerId();
        this.proName = data.getProName();
        this.protocol = data.getProtocol();
        this.clientMac = data.getClientMac();
        this.serverMac = data.getServerMac();
        this.clientIp = data.getClientIp();
        this.serverIp = data.getServerIp();
        this.clientPort = data.getClientPort();
        this.serverPort = data.getServerPort();
        this.clientIpOuter = data.getClientIpOuter();
        this.serverIpOuter = data.getServerIpOuter();
        this.clientPortOuter = data.getClientPortOuter();
        this.serverPortOuter = data.getServerPortOuter();
        this.protocolOuter = data.getProtocolOuter();
        this.imp = data.getImp();
        this.malformedUpPayload = data.getMalformedUpPayload();
        this.malformedDownPayload = data.getMalformedDownPayload();
        this.imsi = data.getImsi();
        this.imei = data.getImei();
        this.msisdn = data.getMsisdn();
        this.dataType = data.getDataType();
        this.groupName = data.getGroupName();
        this.targetName = data.getTargetName();
        this.upPkt = data.getUpPkt();
        this.upByte = data.getUpByte();
        this.downPkt = data.getDownPkt();
        this.downByte = data.getDownByte();
        this.duration = data.getDuration();
        this.syn = data.getSyn();
        this.fin = data.getFin();
        this.foreign = data.getForeign();
        this.eventData = data.getEventData();
        this.caseTags = data.getCaseTags();
        this.id = data.getId();
        this.clientIpN = data.getClientIpN();
        this.serverIpN = data.getServerIpN();
        this.clientLocation = data.getClientLocation();
        this.serverLocation = data.getServerLocation();
        this.tag = data.getTag();
        this.keyWord = data.getKeyWord();
        this.extensionFlag = data.getDataSource();
        this.sessionExtension = data.getSessionExtension();
        this.sslExtension = data.getSslExtension();
        this.openVpnExtension = data.getOpenVpnExtension();
        this.dnsExtension = data.getDnsExtension();
        this.sshExtension = data.getSshExtension();
        this.httpExtension = data.getHttpExtension();
        this.isakmpExtension = data.getIsakmpExtension();
        this.ftpAndTelnetExtension = data.getFtpAndTelnetExtension();
        this.espAndAhExtension = data.getEspAndAhExtension();
        this.malformedExtension = data.getMalformedExtension();
        this.cer = data.getCer();
        this.dnsRequestBO = data.getDnsRequestBO();
        this.dataSource = data.getDataSource();
        this.dataType = data.getDataType();
        this.protocolKnown = data.getProtocolKnown();
        this.appKnown = data.getAppKnown();
        this.malFormed = data.getMalFormed();
        this.foreign = data.getForeign();
        this.enc = data.getEnc();
        this.assetFlag = data.getAssetFlag();
    }

    public String targetSessionKey() {
        return ToolUtils.getMD5(this.targetName + "_" + this.userId + "_" + this.serverIp);
    }

    @Override
    public void adjust() {
    }


}
