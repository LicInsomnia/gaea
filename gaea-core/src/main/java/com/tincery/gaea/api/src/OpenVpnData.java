package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OpenVpnData extends AbstractSrcData {

    /* 会话信息 */
    /**
     * 握手过程
     */
    private List<String> handshake;
    /**
     * 会话中是否存在应用数据 通过拓展字段中是否包含Application Data判别
     */
    private boolean hasApplicationData;
    /**
     * 请求服务名 ServerName
     */
    private String serverName;
    /**
     * 证书认证模式：证书单向认证（只有一方证书链：false），证书双向认证（有双方证书链：true），无证书认证（无证书链：null）
     */
    private Boolean daulAuth;
    /**
     * 协议版本 ssl_version
     */
    private String version;
    /**
     * 协商后算法套件 cipher_suite
     */
    private CipherSuiteDO cipherSuite;
    /**
     * 服务端应用证书SHA1
     */
    private String sha1;

    /* 会话客户端属性信息 */
    /**
     * 客户端证书链 cert
     */
    private List<String> clientCerChain;
    /**
     * 客户端JA3 JA3
     */
    private String clientJA3;
    /**
     * 客户端指纹特征 fingerprint
     */
    private String clientFingerPrint;
    /**
     * 客户端支持的算法套件 CipherSuite
     */
    private String clientCipherSuites;
    /**
     * 客户端HASH算法 HashAlgorithms
     */
    private String clientHashAlgorithms;

    /* 会话服务端属性信息 */
    /**
     * 服务端证书链 cert
     */
    private List<String> serverCerChain;
    /**
     * 服务端JA3 JA3S
     */
    private String serverJA3;
    /**
     * 服务端指纹特征 fingerprint
     */
    private String serverFingerPrint;
    /**
     * 服务端椭圆曲线曲线名称 named_curve
     */
    private String serverECDHNamedCurve;
    /**
     * 服务端椭圆曲线公钥数据 publicKey data
     */
    private String serverECDHPublicKeyData;
    /**
     * 服务端椭圆曲线签名算法 signature algorithm
     */
    private String serverECDHSignatureAlgorithm;
    /**
     * 服务端椭圆曲线签名数据 signature data
     */
    private String serverECDHSignatureData;

    @Override
    public void adjust() {
        super.adjust();
        adjustDaulAuth();
        adjustSha1();
    }

    @Override
    public void adjustCompleteSession() {
        if (this.syn && hasApplicationData) {
            this.completeSession = true;
        }
    }

    private void adjustDaulAuth() {
        if (null == this.clientCerChain && null == this.serverCerChain) {
            return;
        }
        if (null != this.clientCerChain && null != this.serverCerChain) {
            this.completeSession = true;
            return;
        }
        this.completeSession = false;
    }

    private void adjustSha1() {
        if (null == this.serverCerChain) {
            return;
        }
        this.sha1 = this.serverCerChain.get(0);
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.duration, this.syn, this.fin,
                this.malformedUpPayload, this.malformedDownPayload,
                SourceFieldUtils.formatCollection(this.handshake),
                this.hasApplicationData, this.serverName, this.daulAuth,
                this.version, this.cipherSuite, this.sha1,
                SourceFieldUtils.formatCollection(this.clientCerChain),
                this.clientJA3, this.clientFingerPrint, this.clientCipherSuites, this.clientHashAlgorithms,
                SourceFieldUtils.formatCollection(this.serverCerChain),
                this.serverJA3, this.serverFingerPrint, this.serverECDHNamedCurve, this.serverECDHPublicKeyData,
                this.serverECDHSignatureAlgorithm, this.serverECDHSignatureData
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
