package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Insomnia
 */
@Getter
@Setter
@ToString
public class IsakmpExtension implements Serializable {

    @JSONField(serialize = false)
    private String messageListStr = null;
    @JSONField(serialize = false)
    private String initiatorInformationStr = null;
    @JSONField(serialize = false)
    private String responderInformationStr = null;
    @JSONField(serialize = false)
    private String initiatorVidStr = null;
    @JSONField(serialize = false)
    private String responderVidStr = null;
    @JSONField(serialize = false)
    private List<String> messageList;
    @JSONField(serialize = false)
    private Set<JSONObject> initiatorInformation;
    @JSONField(serialize = false)
    private Set<JSONObject> responderInformation;
    @JSONField(serialize = false)
    private Set<JSONObject> initiatorVid;
    @JSONField(serialize = false)
    private Set<JSONObject> responderVid;
    @JSONField(serialize = false)
    private JSONObject extension;

    public void setExtension() {
        this.extension = new JSONObject();
        if (null != this.messageList && !this.messageList.isEmpty()) {
            this.extension.put("messageList", this.messageList);
            this.messageListStr = ToolUtils.convertString(this.messageList, ";");
        }
        if (null != this.initiatorInformation && !this.initiatorInformation.isEmpty()) {
            this.extension.put("initiatorInformation", this.initiatorInformation);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.initiatorInformation) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.initiatorInformationStr = stringBuilder.toString();
        }
        if (null != this.responderInformation && !this.responderInformation.isEmpty()) {
            this.extension.put("responderInformation", this.responderInformation);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.responderInformation) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.responderInformationStr = stringBuilder.toString();
        }
        if (null != this.initiatorVid && !this.initiatorVid.isEmpty()) {
            this.extension.put("initiatorVid", this.initiatorVid);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.initiatorVid) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.initiatorVidStr = stringBuilder.toString();
        }
        if (null != this.responderVid && !this.responderVid.isEmpty()) {
            this.extension.put("responderVid", this.responderVid);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.responderVid) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.responderVidStr = stringBuilder.toString();
        }
    }

    /**
     * 协议版本【protocolVersion】
     * 如果会话是malformed，为"非标准IPSEC"
     * 如果isakmp协商之后使用UDP 4500端口通信，则为“IPSEC VPN（NAT模式）”
     * 否则为"IPSEC VPN"
     */
    private String protocolVersion;
    /**
     * 密钥交换第一阶段模式【firstMode】
     * 如果是messagelist包含"Aggressive"，则为"野蛮模式"；
     * 如果messagelist不包含"Identity Protection (Main Mode)"，并且不包含”Aggressive”，则为"-"
     * 否则为"主模式"
     */
    private String firstMode;
    /**
     * 密钥交换第二阶段模式【secondMode】
     * 如果是messagelist包含"Quick Mode"，则为"快速模式"，否则为”-“
     */
    private String secodeMode;
    /**
     * 发起方密钥交换第一阶段交换数据完整性【initiatorFirstComplete】
     * 如果initiator_information的Private Use Data、Nonce Data、Identification Data、Signature Data均存在，则显示"完整"
     * 如果会话是malformed，则显示"-"，否则显示"不完整"
     */
    private String initiatorFirstComplete;
    /**
     * 响应方密钥交换第一阶段交换数据完整性【responderFirstComplete】
     * 如果responder_information的Private Use Data、Nonce Data、Identification Data、Signature Data均存在，则显示"完整"
     * 会话是malformed，则显示"-"，否则显示"不完整"
     */
    private String responderFirstComplete;
    /**
     * 发起方证书编码【initiatorCertEncoding】
     * initiator_information.cert[i].Cert Encoding
     */
    private String initiatorCertEncoding;
    /**
     * 发起方证书SHA1【initiatorSha1】
     * initiator_information.cert[i].sha1
     */
    private String initiatorSha1;
    /**
     * 响应方证书编码【responderCertEncoding】
     * responder_information.cert[i].Cert Encoding
     */
    private String responderCertEncoding;
    /**
     * 响应方证书SHA1【responderSha1】
     * responder_information.cert[i].sha1
     */
    private String responderSha1;
    /**
     * 第二阶段密钥交换完整性【secondComplete】
     * 如果没有发起方和响应方的“Quick Mode“数据，显示”-“
     * 如果发起方和响应方的“(Exchange Type = Quick Mode) + Message ID + 载荷长度”去重后大于一个，表示完整，显示“完整”
     * 否则，显示“不完整“
     * 注意：发起方和响应方数据放在一起比
     */
    private String secondComplete;
    /**
     * 是否存在加密的鉴别数据【encryptedAuthenticationData】
     * 如果是"Identity Protection (Main Mode) "，并且 “payload: Hash”存在，则为true，否则为false
     */
    private String encryptedAuthenticationData;

    public void adjust(boolean malformed, int protocol, int serverPort) {
        adjustProtocolVersion(malformed, protocol, serverPort);
        adjustFirstMode();
        adjustSecondMode();
        adjustInitiatorFirstComplete(malformed);
        adjustResponderFirstComplete(malformed);
    }

    private void adjustProtocolVersion(boolean malformed, int protocol, int serverPort) {
        if (malformed) {
            this.protocolVersion = "非标准IPSEC";
        } else if (protocol == 17 && serverPort == 4500) {
            this.protocolVersion = "IPSEC VPN（NAT模式）";
        } else {
            this.protocolVersion = "IPSEC VPN";
        }
    }

    private void adjustFirstMode() {
        if (this.messageList.contains("Aggressive")) {
            this.firstMode = "野蛮模式";
        } else if (this.messageList.contains("Identity Protection (Main Mode)")) {
            this.firstMode = "-";
        } else {
            this.firstMode = "主模式";
        }
    }

    private void adjustSecondMode() {
        if (this.messageList.contains("Quick Mode")) {
            this.secodeMode = "快速模式";
        } else {
            this.secodeMode = "-";
        }
    }

    private void adjustInitiatorFirstComplete(boolean malformed) {
        if (malformed) {
            this.initiatorFirstComplete = "-";
            return;
        }
        if (this.initiatorInformation.contains("Private Use Data") &&
                this.initiatorInformation.contains("Nonce Data") &&
                this.initiatorInformation.contains("Identification Data") &&
                this.initiatorInformation.contains("Signature Data")) {
            this.initiatorFirstComplete = "完整";
        } else {
            this.initiatorFirstComplete = "不完整";
        }
    }

    private void adjustResponderFirstComplete(boolean malformed) {
        if (malformed) {
            this.responderFirstComplete = "-";
            return;
        }
        if (this.responderInformation.contains("Private Use Data") &&
                this.responderInformation.contains("Nonce Data") &&
                this.responderInformation.contains("Identification Data") &&
                this.responderInformation.contains("Signature Data")) {
            this.responderFirstComplete = "完整";
        } else {
            this.responderFirstComplete = "不完整";
        }
    }

}
