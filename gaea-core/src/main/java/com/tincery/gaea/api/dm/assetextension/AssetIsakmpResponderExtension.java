package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Data;

import java.util.List;

/**
 * @author Insomnia
 */
@Data
public class AssetIsakmpResponderExtension extends BaseAssetExtension {

    /**
     * 通信协议
     */
    private String proName;
    /**
     * 密钥交换第一阶段模式
     */
    private String firstMode;
    /**
     * 密钥交换第二阶段模式
     */
    private String secondMode;
    /**
     * 加密报文检测
     */
    private String encryptedMessageProtocol;
    /**
     * 响应方密钥交换第一阶段交换数据完整性
     */
    private String responderFirstComplete;
    /**
     * 数据加密算法
     */
    private String responderEncryptionAlgorithm;
    /**
     * 对称密钥长度
     */
    private String responderKeyLength;
    /**
     * 完整性校验算法
     */
    private String responderHashAlgorithm;
    /**
     * 实体认证数字签名算法
     */
    private String responderAuthenticationMethod;
    /**
     * 密钥交换算法
     */
    private String responderKeyExchange;
    /**
     * 生存期时间
     */
    private String responderLifeDuration;
    /**
     * 第二阶段密钥交换完整性
     */
    private String secondComplete;
    /**
     * 密钥交换第一阶段证书
     */
    private List<Object> responderCert;
    private String protocolVersion;
    private String version;

    @Override
    public boolean create(JSONObject jsonObject) {
        JSONObject isakmpExtension = jsonObject.getJSONObject(HeadConst.FIELD.IASKMP_EXTENSION);
        if (null == isakmpExtension) {
            return false;
        }
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.firstMode = isakmpExtension.getString(HeadConst.FIELD.FIRST_MODE);
        this.secondMode = isakmpExtension.getString(HeadConst.FIELD.SECOND_MODE);
        this.encryptedMessageProtocol = isakmpExtension.getString(HeadConst.FIELD.ENCRYPTION_MESSAGE_PROTOCOL);
        this.responderFirstComplete = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_FIRST_COMPLETE);
        this.responderEncryptionAlgorithm = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_ENCRYPTION_ALGORITM);
        this.responderKeyLength = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_KEY_LENGTH);
        this.responderHashAlgorithm = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_HASH_ALGORITHM);
        this.responderAuthenticationMethod = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_AUTHENTICATION_METHOD);
        this.responderKeyExchange = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_KEY_EXCHANGE);
        this.responderLifeDuration = isakmpExtension.getString(HeadConst.FIELD.RESPONDER_LIFE_DURATION);
        this.secondComplete = isakmpExtension.getString(HeadConst.FIELD.SECOND_COMPLETE);
        this.responderCert = isakmpExtension.getJSONArray(HeadConst.FIELD.RESPONDER_CERT);
        this.protocolVersion = isakmpExtension.getString(HeadConst.FIELD.PROTOCOL_VERSION);
        this.version = isakmpExtension.getString(HeadConst.FIELD.ISAKMP_VERSION);
        setKey();
        appendFlow(jsonObject);
        return true;
    }

    @Override
    public void setKey() {
        this.id = ToolUtils.getMD5(this.proName + "_" + this.firstMode + "_" + this.secondMode + "_" +
                this.encryptedMessageProtocol + "_" + this.responderFirstComplete + "_" +
                this.responderEncryptionAlgorithm + "_" + this.responderKeyLength + "_" +
                this.responderHashAlgorithm + "_" + this.responderAuthenticationMethod + "_" +
                this.responderKeyExchange + "_" + this.responderLifeDuration + "_" +
                this.secondComplete + "_" + this.responderCert + "_" +
                this.protocolVersion + "_" + this.version);
    }

}
