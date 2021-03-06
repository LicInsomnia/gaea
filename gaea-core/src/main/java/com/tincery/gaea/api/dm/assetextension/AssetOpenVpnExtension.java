package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Data;

@Data
public class AssetOpenVpnExtension extends BaseAssetExtension {

    /**
     * 通信协议
     */
    private String proName;
    /**
     * 密钥交换算法
     */
    private String finalKexAlgorithms;
    /**
     * 服务器主机密钥算法
     */
    private String finalServerHostKeyAlgorithms;
    /**
     * 客户端到服务端加密算法
     */
    private String finalEncryptionAlgorithmsClient2Server;
    /**
     * 服务端到客户端加密算法
     */
    private String finalEncryptionAlgorithmsServer2Client;
    /**
     * 客户端到服务端消息验证码算法
     */
    private String finalMacAlgorithmsClient2Server;
    /**
     * 服务端到客户端消息验证码算法
     */
    private String finalMacAlgorithmsServer2Client;
    /**
     * 客户端到服务端压缩算法
     */
    private String finalCompressionAlgorithmsClient2Server;
    /**
     * 服务端到客户端压缩算法
     */
    private String finalCompressionAlgorithmsServer2Client;

    @Override
    public boolean create(JSONObject jsonObject) {
        JSONObject sshExtension = jsonObject.getJSONObject(HeadConst.FIELD.SSH_EXTENSION);
        if (null == sshExtension) {
            return false;
        }
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.finalKexAlgorithms = sshExtension.getString(HeadConst.FIELD.FINAL_KEX_ALGORITHMS);
        this.finalServerHostKeyAlgorithms = sshExtension.getString(HeadConst.FIELD.FINAL_SERVER_HOST_KEY_ALGORITHMS);
        this.finalEncryptionAlgorithmsClient2Server = sshExtension.getString(HeadConst.FIELD.FINAL_ENCRYPTION_ALGORITHMS_CLIENT_TO_SERVER);
        this.finalEncryptionAlgorithmsServer2Client = sshExtension.getString(HeadConst.FIELD.FINAL_ENCRYPTION_ALGORITHMS_SERVER_TO_CLIENT);
        this.finalMacAlgorithmsClient2Server = sshExtension.getString(HeadConst.FIELD.FINAL_MAC_ALGORITHMS_SERVER_TO_CLIENT);
        this.finalMacAlgorithmsServer2Client = sshExtension.getString(HeadConst.FIELD.FINAL_MAC_ALGORITHMS_SERVER_TO_CLIENT);
        this.finalCompressionAlgorithmsClient2Server = sshExtension.getString(HeadConst.FIELD.FINAL_COMPRESSION_ALGORITHMS_CLIENT_TO_SERVER);
        this.finalCompressionAlgorithmsServer2Client = sshExtension.getString(HeadConst.FIELD.FINAL_COMPRESSION_ALGORITHMS_SERVER_TO_CLIENT);
        setKey();
        appendFlow(jsonObject);
        return true;
    }

    @Override
    public void setKey() {
        this.id = ToolUtils.getMD5(this.proName + "_" + this.finalKexAlgorithms + "_" +
                this.finalServerHostKeyAlgorithms + "_" + this.finalEncryptionAlgorithmsClient2Server + "_" +
                this.finalEncryptionAlgorithmsServer2Client + "_" + this.finalMacAlgorithmsClient2Server + "_" +
                this.finalMacAlgorithmsServer2Client + "_" + this.finalCompressionAlgorithmsClient2Server + "_" +
                this.finalCompressionAlgorithmsServer2Client
        );
    }

}
