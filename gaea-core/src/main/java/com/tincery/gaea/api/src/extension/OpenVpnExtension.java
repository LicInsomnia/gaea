package com.tincery.gaea.api.src.extension;

import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class OpenVpnExtension extends SslExtension implements Serializable {

    @SuppressWarnings("unchecked")
    public void merge(OpenVpnExtension openVpnExtension) {
        if (null == openVpnExtension) {
            return;
        }
        this.handshake.merge(openVpnExtension.handshake);
        this.hasApplicationData = this.hasApplicationData || openVpnExtension.hasApplicationData;
        this.serverName = (String) SourceFieldUtils.mergeField(this.serverName, openVpnExtension.serverName);
        this.version = (String) SourceFieldUtils.mergeField(this.version, openVpnExtension.version);
        this.cipherSuite = (CipherSuiteDO) SourceFieldUtils.mergeField(this.cipherSuite, openVpnExtension.cipherSuite);
        this.sha1 = (String) SourceFieldUtils.mergeField(this.sha1, openVpnExtension.sha1);
        this.clientCerChain = (List<SslCer>) SourceFieldUtils.mergeField(this.clientCerChain, openVpnExtension.clientCerChain);
        this.clientJA3 = (String) SourceFieldUtils.mergeField(this.clientJA3, openVpnExtension.clientJA3);
        this.clientFingerPrint = (String) SourceFieldUtils.mergeField(this.clientFingerPrint, openVpnExtension.clientFingerPrint);
        this.clientCipherSuites = (String) SourceFieldUtils.mergeField(this.clientCipherSuites, openVpnExtension.clientCipherSuites);
        this.clientHashAlgorithms = (String) SourceFieldUtils.mergeField(this.clientHashAlgorithms, openVpnExtension.clientHashAlgorithms);
        this.serverCerChain = (List<SslCer>) SourceFieldUtils.mergeField(this.serverCerChain, openVpnExtension.serverCerChain);
        this.serverJA3 = (String) SourceFieldUtils.mergeField(this.serverJA3, openVpnExtension.serverJA3);
        this.serverFingerPrint = (String) SourceFieldUtils.mergeField(this.serverFingerPrint, openVpnExtension.serverFingerPrint);
        this.serverECDHNamedCurve = (String) SourceFieldUtils.mergeField(this.serverECDHNamedCurve, openVpnExtension.serverECDHNamedCurve);
        this.serverECDHPublicKeyData = (String) SourceFieldUtils.mergeField(this.serverECDHPublicKeyData, openVpnExtension.serverECDHPublicKeyData);
        this.serverECDHSignatureAlgorithm = (String) SourceFieldUtils.mergeField(this.serverECDHSignatureAlgorithm, openVpnExtension.serverECDHSignatureAlgorithm);
        this.serverECDHSignatureData = (String) SourceFieldUtils.mergeField(this.serverECDHSignatureData, openVpnExtension.serverECDHSignatureData);
    }

}
