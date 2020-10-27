package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.gaea.api.base.Handshake;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SslExtension {

    /* 会话信息 */
    /**
     * 握手过程
     */
    protected Handshake handshake;
    /**
     * 会话中是否存在应用数据 通过拓展字段中是否包含Application Data判别
     */
    protected boolean hasApplicationData;
    /**
     * 请求服务名 ServerName
     */
    protected String serverName;
    /**
     * 证书认证模式：证书单向认证（只有一方证书链：false），证书双向认证（有双方证书链：true），无证书认证（无证书链：null）
     */
    protected Boolean daulAuth;
    /**
     * 协议版本 ssl_version
     */
    protected String version;
    /**
     * 协商后算法套件 cipher_suite
     */
    protected CipherSuiteDO cipherSuite;
    /**
     * 服务端应用证书SHA1
     */
    protected String sha1;

    /* 会话客户端属性信息 */
    /**
     * 客户端证书链 cert
     */
    protected List<String> clientCerChain;
    /**
     * 客户端JA3 JA3
     */
    protected String clientJA3;
    /**
     * 客户端指纹特征 fingerprint
     */
    protected String clientFingerPrint;
    /**
     * 客户端支持的算法套件 CipherSuite
     */
    protected String clientCipherSuites;
    /**
     * 客户端HASH算法 HashAlgorithms
     */
    protected String clientHashAlgorithms;

    /* 会话服务端属性信息 */
    /**
     * 服务端证书链 cert
     */
    protected List<String> serverCerChain;
    /**
     * 服务端JA3 JA3S
     */
    protected String serverJA3;
    /**
     * 服务端指纹特征 fingerprint
     */
    protected String serverFingerPrint;
    /**
     * 服务端椭圆曲线曲线名称 named_curve
     */
    protected String serverECDHNamedCurve;
    /**
     * 服务端椭圆曲线公钥数据 publicKey data
     */
    protected String serverECDHPublicKeyData;
    /**
     * 服务端椭圆曲线签名算法 signature algorithm
     */
    protected String serverECDHSignatureAlgorithm;
    /**
     * 服务端椭圆曲线签名数据 signature data
     */
    protected String serverECDHSignatureData;

    public String toCsv(char splitChar) {
        String cipherSuites = null == this.cipherSuite ? "" : this.cipherSuite.toCsv(splitChar);
        Object[] join = new Object[]{
                JSONObject.toJSONString(this.handshake),
                this.hasApplicationData, this.serverName, this.daulAuth,
                this.version, cipherSuites, this.sha1,
                SourceFieldUtils.formatCollection(this.clientCerChain),
                this.clientJA3, this.clientFingerPrint, this.clientCipherSuites, this.clientHashAlgorithms,
                SourceFieldUtils.formatCollection(this.serverCerChain),
                this.serverJA3, this.serverFingerPrint, this.serverECDHNamedCurve, this.serverECDHPublicKeyData,
                this.serverECDHSignatureAlgorithm, this.serverECDHSignatureData
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
