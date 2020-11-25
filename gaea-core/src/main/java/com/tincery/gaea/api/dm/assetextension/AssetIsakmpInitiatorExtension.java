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
public class AssetIsakmpInitiatorExtension extends BaseAssetExtension {

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
    private String initiatorFirstComplete;
    /**
     * 数据加密算法
     */
    private String initiatorEncryptionAlgorithm;
    /**
     * 对称密钥长度
     */
    private String initiatorKeyLength;
    /**
     * 完整性校验算法
     */
    private String initiatorHashAlgorithm;
    /**
     * 实体认证数字签名算法
     */
    private String initiatorAuthenticationMethod;
    /**
     * 密钥交换算法
     */
    private String initiatorKeyExchange;
    /**
     * 生存期时间
     */
    private JSONObject initiatorLife;
    /**
     * 第二阶段密钥交换完整性
     */
    private String secondComplete;
    /**
     * 密钥交换第一阶段证书
     */
    private List<Object> initiatorIsakmpCer;
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
        this.initiatorFirstComplete = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_FIRST_COMPLETE);
        this.initiatorEncryptionAlgorithm = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_ENCRYPTION_ALGORITM);
        this.initiatorKeyLength = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_KEY_LENGTH);
        this.initiatorHashAlgorithm = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_HASH_ALGORITHM);
        this.initiatorAuthenticationMethod = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_AUTHENTICATION_METHOD);
        this.initiatorKeyExchange = isakmpExtension.getString(HeadConst.FIELD.INITIATOR_KEY_EXCHANGE);
        this.initiatorLife = isakmpExtension.getJSONObject(HeadConst.FIELD.INITIATOR_LIFE);
        this.secondComplete = isakmpExtension.getString(HeadConst.FIELD.SECOND_COMPLETE);
        this.initiatorIsakmpCer = isakmpExtension.getJSONArray(HeadConst.FIELD.INITIATOR_ISAKMP_CER);
        this.protocolVersion = isakmpExtension.getString(HeadConst.FIELD.PROTOCOL_VERSION);
        this.version = isakmpExtension.getString(HeadConst.FIELD.ISAKMP_VERSION);
        setKey();
        appendFlow(jsonObject);
        return true;
    }

    @Override
    public void setKey() {
        this.id = ToolUtils.getMD5(this.proName + "_" + this.firstMode + "_" + this.secondMode + "_" +
                this.encryptedMessageProtocol + "_" + this.initiatorFirstComplete + "_" +
                this.initiatorEncryptionAlgorithm + "_" + this.initiatorKeyLength + "_" +
                this.initiatorHashAlgorithm + "_" + this.initiatorAuthenticationMethod + "_" +
                this.initiatorKeyExchange + "_" + this.initiatorLife + "_" +
                this.secondComplete + "_" + this.initiatorIsakmpCer + "_" +
                this.protocolVersion + "_" + this.version);
    }

}
