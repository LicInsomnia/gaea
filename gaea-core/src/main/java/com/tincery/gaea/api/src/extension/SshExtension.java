package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SshExtension {

    private List<String> messageList;

    private List<String> clientProtocol;
    private List<String> serverProtocol;
    private List<String> clientKexAlgorithms;
    private List<String> serverKexAlgorithms;
    private List<String> finalKexAlgorithms;
    private List<String> clientServerHostKeyAlgorithms;
    private List<String> serverServerHostKeyAlgorithms;
    private List<String> finalServerHostKeyAlgorithms;
    private List<String> clientEncryptionAlgorithmsClientToServer;
    private List<String> serverEncryptionAlgorithmsClientToServer;
    private List<String> finalEncryptionAlgorithmsClientToServer;
    private List<String> clientEncryptionAlgorithmsServerToClient;
    private List<String> serverEncryptionAlgorithmsServerToClient;
    private List<String> finalEncryptionAlgorithmsServerToClient;
    private List<String> clientMacAlgorithmsClientToServer;
    private List<String> serverMacAlgorithmsClientToServer;
    private List<String> finalMacAlgorithmsClientToServer;
    private List<String> clientMacAlgorithmsServerToClient;
    private List<String> serverMacAlgorithmsServerToClient;
    private List<String> finalMacAlgorithmsServerToClient;
    private List<String> clientCompressionAlgorithmsClientToServer;
    private List<String> serverCompressionAlgorithmsClientToServer;
    private List<String> finalCompressionAlgorithmsClientToServer;
    private List<String> serverCompressionAlgorithmsServerToClient;
    private List<String> clientCompressionAlgorithmsServerToClient;
    private List<String> finalCompressionAlgorithmsServerToClient;
    private List<String> clientPublicKey;
    private List<String> serverPublicKey;
    private List<String> finalPublicKeyAlgorithms;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{SourceFieldUtils.formatCollection(this.clientProtocol),
                SourceFieldUtils.formatCollection(this.serverProtocol), SourceFieldUtils.formatCollection(this.clientKexAlgorithms),
                SourceFieldUtils.formatCollection(this.serverKexAlgorithms), SourceFieldUtils.formatCollection(this.finalKexAlgorithms),
                SourceFieldUtils.formatCollection(this.clientServerHostKeyAlgorithms), SourceFieldUtils.formatCollection(this.serverServerHostKeyAlgorithms),
                SourceFieldUtils.formatCollection(this.finalServerHostKeyAlgorithms), SourceFieldUtils.formatCollection(this.clientEncryptionAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.serverEncryptionAlgorithmsClientToServer), SourceFieldUtils.formatCollection(this.finalEncryptionAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.clientEncryptionAlgorithmsServerToClient), SourceFieldUtils.formatCollection(this.serverEncryptionAlgorithmsServerToClient),
                SourceFieldUtils.formatCollection(this.finalEncryptionAlgorithmsServerToClient), SourceFieldUtils.formatCollection(this.clientMacAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.serverMacAlgorithmsClientToServer), SourceFieldUtils.formatCollection(this.finalMacAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.clientMacAlgorithmsServerToClient), SourceFieldUtils.formatCollection(this.serverMacAlgorithmsServerToClient),
                SourceFieldUtils.formatCollection(this.finalMacAlgorithmsServerToClient), SourceFieldUtils.formatCollection(this.clientCompressionAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.serverCompressionAlgorithmsClientToServer), SourceFieldUtils.formatCollection(this.finalCompressionAlgorithmsClientToServer),
                SourceFieldUtils.formatCollection(this.serverCompressionAlgorithmsServerToClient), SourceFieldUtils.formatCollection(this.clientCompressionAlgorithmsServerToClient),
                SourceFieldUtils.formatCollection(this.finalCompressionAlgorithmsServerToClient)};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
