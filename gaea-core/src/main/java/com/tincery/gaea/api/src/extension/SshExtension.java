package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SshExtension implements Serializable {

    private List<String> messageList;

    private String clientProtocol;
    private String serverProtocol;
    private String clientKexAlgorithms;
    private String serverKexAlgorithms;
    private String finalKexAlgorithms;
    private String clientServerHostKeyAlgorithms;
    private String serverServerHostKeyAlgorithms;
    private String finalServerHostKeyAlgorithms;
    private String clientEncryptionAlgorithmsClientToServer;
    private String serverEncryptionAlgorithmsClientToServer;
    private String finalEncryptionAlgorithmsClientToServer;
    private String clientEncryptionAlgorithmsServerToClient;
    private String serverEncryptionAlgorithmsServerToClient;
    private String finalEncryptionAlgorithmsServerToClient;
    private String clientMacAlgorithmsClientToServer;
    private String serverMacAlgorithmsClientToServer;
    private String finalMacAlgorithmsClientToServer;
    private String clientMacAlgorithmsServerToClient;
    private String serverMacAlgorithmsServerToClient;
    private String finalMacAlgorithmsServerToClient;
    private String clientCompressionAlgorithmsClientToServer;
    private String serverCompressionAlgorithmsClientToServer;
    private String finalCompressionAlgorithmsClientToServer;
    private String serverCompressionAlgorithmsServerToClient;
    private String clientCompressionAlgorithmsServerToClient;
    private String finalCompressionAlgorithmsServerToClient;
    private String clientPublicKey;
    private String serverPublicKey;
    private String finalPublicKeyAlgorithms;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{SourceFieldUtils.parseStringStrEmptyToNull(this.clientProtocol),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverProtocol),
                SourceFieldUtils.parseStringStrEmptyToNull(this.clientKexAlgorithms),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverKexAlgorithms), SourceFieldUtils.parseStringStrEmptyToNull(this.finalKexAlgorithms),
                SourceFieldUtils.parseStringStrEmptyToNull(this.clientServerHostKeyAlgorithms), SourceFieldUtils.parseStringStrEmptyToNull(this.serverServerHostKeyAlgorithms),
                SourceFieldUtils.parseStringStrEmptyToNull(this.finalServerHostKeyAlgorithms), SourceFieldUtils.parseStringStrEmptyToNull(this.clientEncryptionAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverEncryptionAlgorithmsClientToServer), SourceFieldUtils.parseStringStrEmptyToNull(this.finalEncryptionAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.clientEncryptionAlgorithmsServerToClient), SourceFieldUtils.parseStringStrEmptyToNull(this.serverEncryptionAlgorithmsServerToClient),
                SourceFieldUtils.parseStringStrEmptyToNull(this.finalEncryptionAlgorithmsServerToClient), SourceFieldUtils.parseStringStrEmptyToNull(this.clientMacAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverMacAlgorithmsClientToServer), SourceFieldUtils.parseStringStrEmptyToNull(this.finalMacAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.clientMacAlgorithmsServerToClient), SourceFieldUtils.parseStringStrEmptyToNull(this.serverMacAlgorithmsServerToClient),
                SourceFieldUtils.parseStringStrEmptyToNull(this.finalMacAlgorithmsServerToClient), SourceFieldUtils.parseStringStrEmptyToNull(this.clientCompressionAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverCompressionAlgorithmsClientToServer), SourceFieldUtils.parseStringStrEmptyToNull(this.finalCompressionAlgorithmsClientToServer),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverCompressionAlgorithmsServerToClient), SourceFieldUtils.parseStringStrEmptyToNull(this.clientCompressionAlgorithmsServerToClient),
                SourceFieldUtils.parseStringStrEmptyToNull(this.finalCompressionAlgorithmsServerToClient),SourceFieldUtils.parseStringStrEmptyToNull(this.clientPublicKey),
                SourceFieldUtils.parseStringStrEmptyToNull(this.serverPublicKey),SourceFieldUtils.parseStringStrEmptyToNull(this.finalPublicKeyAlgorithms)};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
