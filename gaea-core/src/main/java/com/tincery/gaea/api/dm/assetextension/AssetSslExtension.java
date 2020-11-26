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
public class AssetSslExtension extends BaseAssetExtension {

    /**
     * 通信协议
     */
    private String proName;
    /**
     * 服务名
     */
    private String serverName;
    /**
     * 协议版本
     */
    private String version;
    /**
     * 握手流程
     */
    private JSONObject handshake;
    private String handshakeKey;
    private String handshakeDescription;
    /**
     * 协商服务器选择算法套件
     */
    private JSONObject cipherSuite;
    private String cipherSuiteKey;
    private String keyExchangeAlgorithm;
    private String authenticationAlgorithm;
    private String encryptionAlgorithm;
    private String messageAuthenticationCodesAlgorithm;
    /**
     * 服务器证书
     */
    private List<JSONObject> cerChain;
    private String cerChainKey;

    @Override
    @SuppressWarnings("unchecked")
    public boolean create(JSONObject jsonObject) {
        JSONObject sslExtension = jsonObject.getJSONObject(HeadConst.FIELD.SSL_EXTENSION);
        if (null == sslExtension) {
            return false;
        }
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.serverName = sslExtension.getString(HeadConst.FIELD.SERVER_NAME);
        this.version = sslExtension.getString(HeadConst.FIELD.VERSION);
        this.handshake = sslExtension.getJSONObject(HeadConst.FIELD.HANDSHAKE);
        this.cipherSuite = sslExtension.getJSONObject(HeadConst.FIELD.CIPHER_SUITES);
        if (null != this.cipherSuite) {
            this.cipherSuiteKey = cipherSuite.getString(HeadConst.FIELD.ID);
            this.keyExchangeAlgorithm = cipherSuite.getString(HeadConst.FIELD.KEY_EXCHANGE_ALGORITHM);
            this.authenticationAlgorithm = cipherSuite.getString(HeadConst.FIELD.AUTHENTICATION_ALGORITHM);
            this.encryptionAlgorithm = cipherSuite.getString(HeadConst.FIELD.ENCRYPTION_ALGORITHM);
            this.messageAuthenticationCodesAlgorithm = cipherSuite.getString(HeadConst.FIELD.MESSAGE_AUTHENTICATION_CODES_ALGORITHM);
        }
        this.cerChain = (List<JSONObject>) sslExtension.get(HeadConst.FIELD.SERVER_CER_CHAIN);
        if (null != cerChain) {
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject json : cerChain) {
                stringBuilder.append(json.getString("sha1")).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.cerChainKey = stringBuilder.toString();
        }
        setHandshakeDescription();
        setKey();
        appendFlow(jsonObject);
        return true;
    }

    private void setHandshakeDescription() {
        if (null == this.handshake) {
            this.handshakeDescription = "未见握手过程";
            return;
        }
        boolean clientHello = this.handshake.getInteger("clientHello") >= 0;
        boolean serverHello = this.handshake.getInteger("serverHello") >= 0;
        boolean serverCertificate = this.handshake.getInteger("serverCertificate") >= 0;
        boolean serverKeyExchange = this.handshake.getInteger("serverKeyExchange") >= 0;
        boolean serverCertificateRequest = this.handshake.getInteger("serverCertificateRequest") >= 0;
        boolean serverHelloDone = this.handshake.getInteger("serverHelloDone") >= 0;
        boolean clientCertificate = this.handshake.getInteger("clientCertificate") >= 0;
        boolean clientKeyExchange = this.handshake.getInteger("clientKeyExchange") >= 0;
        boolean clientCertificateVerify = this.handshake.getInteger("clientCertificateVerify") >= 0;
        boolean clientFinished = this.handshake.getInteger("clientFinished") >= 0;
        boolean serverFinished = this.handshake.getInteger("serverFinished") >= 0;
        boolean clientChangeCipherSpec = this.handshake.getInteger("clientChangeCipherSpec") >= 0;
        boolean serverChangeCipherSpec = this.handshake.getInteger("serverChangeCipherSpec") >= 0;
        if (clientHello && serverHello && clientFinished && serverFinished) {
            this.handshakeDescription = "握手过程完整";
        } else {
            this.handshakeDescription = "握手过程不完整";
        }
        this.handshakeKey = ToolUtils.getMD5(clientHello + "_" + serverHello + "_" + serverCertificate + "_" + serverKeyExchange + "_" +
                serverCertificateRequest + "_" + serverHelloDone + "_" + clientCertificate + "_" + clientKeyExchange + "_" + clientCertificateVerify + "_" +
                clientFinished + "_" + serverFinished + "_" + clientChangeCipherSpec + "_" + serverChangeCipherSpec);
    }

    @Override
    public void setKey() {
        this.id = ToolUtils.getMD5(this.serverName + "_" + this.proName + "_" +
                this.version + "_" + this.handshakeKey + "_" + this.cipherSuiteKey + this.cerChainKey);
    }

}
