package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Insomnia
 */
@Getter
@Setter
@ToString
public class IsakmpExtension implements Serializable {

    private List<String> messageList;
    private Set<JSONObject> initiatorInformation;
    private Set<JSONObject> responderInformation;
    private Set<JSONObject> initiatorVid;
    private Set<JSONObject> responderVid;
    private Integer version;
    private String encryptedMessageProtocol;

    /**
     * initiator属性
     */
    private Set<IsakmpCer> initiatorIsakmpCer;
    private Integer initiatorLifeDuration;
    private String initiatorKeyExchange;
    private String initiatorAuthenticationMethod;
    private String initiatorHashAlgorithm;
    private Integer initiatorKeyLength;
    private String initiatorEncryptionAlgorithm;
    /**
     * responder属性
     */
    private Set<IsakmpCer> responderIsakmpCer;
    private Integer responderLifeDuration;
    private String responderKeyExchange;
    private String responderAuthenticationMethod;
    private String responderHashAlgorithm;
    private Integer responderKeyLength;
    private String responderEncryptionAlgorithm;
    private String initiatorSPI;
    private String responderSPI;

    public void setExtension() {
        setInitiator();
        setResponder();
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
    private String secondMode;
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
    private Boolean encryptedAuthenticationData;

    private void setInitiator() {
        if (null == this.initiatorInformation) {
            return;
        }
        for (JSONObject jsonObject : this.initiatorInformation) {
            if (jsonObject.containsKey("SHA1") && jsonObject.containsKey("Cert-Encoding")) {
                IsakmpCer isakmpCer = new IsakmpCer(jsonObject.getString("SHA1"), jsonObject.getString("Cert-Encoding"));
                if (null == this.initiatorIsakmpCer) {
                    this.initiatorIsakmpCer = new HashSet<>();
                }
                this.initiatorIsakmpCer.add(isakmpCer);
            }
            if (jsonObject.containsKey("Life-Duration")) {
                this.initiatorLifeDuration = jsonObject.getInteger("Life-Duration");
            }
            if (jsonObject.containsKey("Key-Exchange")) {
                this.initiatorKeyExchange = jsonObject.getString("Key-Exchange");
            }
            if (jsonObject.containsKey("Authentication-Method")) {
                this.initiatorAuthenticationMethod = jsonObject.getString("Authentication-Method");
            }
            if (jsonObject.containsKey("Hash-Algorithm")) {
                this.initiatorHashAlgorithm = jsonObject.getString("Hash-Algorithm");
            }
            if (jsonObject.containsKey("Key-Length")) {
                this.initiatorKeyLength = jsonObject.getInteger("Key-Length");
            }
            if (jsonObject.containsKey("Encryption-Algorithm")) {
                this.initiatorEncryptionAlgorithm = jsonObject.getString("Encryption-Algorithm");
            }
        }
    }

    private void setResponder() {
        if (null == this.responderInformation) {
            return;
        }
        for (JSONObject jsonObject : this.responderInformation) {
            if (jsonObject.containsKey("SHA1") && jsonObject.containsKey("Cert-Encoding")) {
                IsakmpCer isakmpCer = new IsakmpCer(jsonObject.getString("SHA1"), jsonObject.getString("Cert-Encoding"));
                if (null == this.responderIsakmpCer) {
                    this.responderIsakmpCer = new HashSet<>();
                }
                this.responderIsakmpCer.add(isakmpCer);
            }
            if (jsonObject.containsKey("Life-Duration")) {
                this.responderLifeDuration = jsonObject.getInteger("Life-Duration");
            }
            if (jsonObject.containsKey("Key-Exchange")) {
                this.responderKeyExchange = jsonObject.getString("Key-Exchange");
            }
            if (jsonObject.containsKey("Authentication-Method")) {
                this.responderAuthenticationMethod = jsonObject.getString("Authentication-Method");
            }
            if (jsonObject.containsKey("Hash-Algorithm")) {
                this.responderHashAlgorithm = jsonObject.getString("Hash-Algorithm");
            }
            if (jsonObject.containsKey("Key-Length")) {
                this.responderKeyLength = jsonObject.getInteger("Key-Length");
            }
            if (jsonObject.containsKey("Encryption-Algorithm")) {
                this.responderEncryptionAlgorithm = jsonObject.getString("Encryption-Algorithm");
            }
        }
    }

    public void adjust(boolean malformed, int protocol, int serverPort) {
        adjustProtocolVersion(malformed, protocol, serverPort);
        adjustFirstMode();
        adjustSecondMode();
        adjustInitiatorFirstComplete(malformed);
        adjustResponderFirstComplete(malformed);
        adjustEncryptionDiscernData();
        adjustSecondComplete();
    }

    /**
     * 第二阶段密钥完整性
     */
    private void adjustSecondComplete() {
        Set<JSONObject> resultSet = new HashSet<>(this.initiatorInformation);
        resultSet.addAll(this.responderInformation);
        List<JSONObject> collect = resultSet.stream().filter(item -> Objects.equals("Quick Mode", item.get("Exchange Type")))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            this.secondComplete = "-";
        }
        List<String> list = collect.stream().map(item -> {
            Object messageId = item.get("Message ID");
            Object length = item.get("Length");
            if (Objects.nonNull(messageId) && Objects.nonNull(length)) {
                return messageId.toString() + length.toString();
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!list.isEmpty() && list.size() > 1) {
            this.secondComplete = "完整";
        } else {
            this.secondComplete = "不完整";
        }

    }

    /*是否存在加密鉴别数据 */
    private void adjustEncryptionDiscernData() {
        Set<JSONObject> resultSet = new HashSet<>(this.initiatorInformation);
        resultSet.addAll(this.responderInformation);
        resultSet.stream().filter(item -> Objects.nonNull(item.get("Exchange Type")))
                .forEach(jsonObject -> {
                    if (encryptionDiscernData(jsonObject)) {
                        this.encryptedAuthenticationData = true;
                    }
                });
        if (!Objects.equals(this.encryptedAuthenticationData, true)) {
            this.encryptedAuthenticationData = false;
        }
    }

    private boolean encryptionDiscernData(JSONObject jsonObject) {
        if (!jsonObject.isEmpty()) {
            if (Objects.equals("Identity Protection (Main Mode)", jsonObject.get("Exchange Type"))) {
                Object payload = jsonObject.get("payload");
                return Objects.nonNull(payload);
            }
        }
        return false;
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
        if (this.messageList.contains("initiator:Aggressive") || this.messageList.contains("responder:Aggressive")) {
            this.firstMode = "野蛮模式";
        } else if (this.messageList.contains("initiator:Identity Protection (Main Mode)") || this.messageList.contains("responder:Identity Protection (Main Mode)")) {
            this.firstMode = "主模式";
        } else {
            this.firstMode = "-";
        }
    }

    private void adjustSecondMode() {
        if (this.messageList.contains("initiator:Quick Mode") || this.messageList.contains("responder:Quick Mode")) {
            this.secondMode = "快速模式";
        } else {
            this.secondMode = "-";
        }
    }


    private void adjustInitiatorFirstComplete(boolean malformed) {
        if (malformed) {
            this.initiatorFirstComplete = "-";
            return;
        }
        Set<String> set = new HashSet<>();
        this.initiatorInformation.stream().map(JSONObject::keySet).forEach(set::addAll);

        if (set.contains("Private Use Data") &&
                set.contains("Nonce Data") &&
                set.contains("Identification Data") &&
                set.contains("Signature Data")) {
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
        Set<String> set = new HashSet<>();
        this.responderInformation.stream().map(JSONObject::keySet).forEach(set::addAll);
        if (set.contains("Private Use Data") &&
                set.contains("Nonce Data") &&
                set.contains("Identification Data") &&
                set.contains("Signature Data")) {
            this.initiatorFirstComplete = "完整";
        } else {
            this.responderFirstComplete = "不完整";
        }
    }

    public String convertCert(String value) {
        switch (value) {
            case "PKCS #7 wrapped X.509 certificate":
                value = "PKCS#7包装的X.509证书";
                break;
            case "PGP Certificate":
                value = "PGP证书";
                break;
            case "DNS Signed Key":
                value = "DNS签名密钥";
                break;
            case "X.509 Certificate - Signature":
                value = "X.509签名证书";
                break;
            case "X.509 Certificate - Key Exchange":
                value = "X.509加密证书";
                break;
            case "Kerberos Tokens":
                value = "Kerberos令牌";
                break;
            case "Certificate Revocation List (CRL)":
                value = "证书吊销列表（CRL）";
                break;
            case "Authority Revocation List (ARL)":
                value = "授权撤销列表（ARL）";
                break;
            case "SPKI Certificate":
                value = "SPKI证书";
                break;
            case "X.509 Certificate - Attribute":
                value = "X.509属性证书";
                break;
            default:
                break;
        }
        return value;
    }

}
