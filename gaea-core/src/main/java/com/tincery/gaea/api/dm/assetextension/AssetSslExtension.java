package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Data;

import java.util.List;

@Data
public class AssetSslExtension extends BaseAssetExtension {

    /**
     * 表格行中字段，用于去重
     */
    private String serverName;
    private String protocolDescription;
    private String versionDescription;
    private String handshakeDescription;
    private String handshakeKey;
    private JSONObject cipherSuite;
    private String cipherSuiteKey;
    private String keyExchangeAlgorithm;
    private String authenticationAlgorithm;
    private String encryptionAlgorithm;
    private String messageAuthenticationCodesAlgorithm;


    private String cerChainDescription;

    private JSONObject handshake;

    public void append(JSONObject jsonObject) {
        JSONObject sslExtension = jsonObject.getJSONObject(HeadConst.FIELD.SSL_EXTENSION);
        this.protocolDescription = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.serverName = sslExtension.getString(HeadConst.FIELD.SERVER_NAME);
        this.versionDescription = sslExtension.getString(HeadConst.FIELD.VERSION);
        this.handshake = sslExtension.getJSONObject(HeadConst.FIELD.HANDSHAKE);
        this.cipherSuite = sslExtension.getJSONObject(HeadConst.FIELD.CIPHER_SUITES);
        this.cipherSuiteKey = cipherSuite.getString(HeadConst.FIELD.ID);
        this.keyExchangeAlgorithm = cipherSuite.getString(HeadConst.FIELD.KEY_EXCHANGE_ALGORITHM);
        this.authenticationAlgorithm = cipherSuite.getString(HeadConst.FIELD.AUTHENTICATION_ALGORITHM);
        this.encryptionAlgorithm = cipherSuite.getString(HeadConst.FIELD.ENCRYPTION_ALGORITHM);
        this.messageAuthenticationCodesAlgorithm = cipherSuite.getString(HeadConst.FIELD.MESSAGE_AUTHENTICATION_CODES_ALGORITHM);
        List<String> serverCerChain = (List<String>) sslExtension.get(HeadConst.FIELD.SERVER_CER_CHAIN);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : serverCerChain) {
            stringBuilder.append(s.split("_")[0]).append("_");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        this.cerChainDescription = stringBuilder.toString();
        setHandshakeDescription();
    }

    private void setHandshakeDescription() {
        if (null == this.handshake) {
            this.handshakeDescription = "未见握手过程";
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
    public BaseAssetExtension merge(BaseAssetExtension that) {
        super.merge(that);
        return this;
    }

}
