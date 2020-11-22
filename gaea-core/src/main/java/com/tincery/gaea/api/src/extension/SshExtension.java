package com.tincery.gaea.api.src.extension;

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
    private String clientEncryptionAlgorithmsClient2Server;
    private String serverEncryptionAlgorithmsClient2Server;
    private String finalEncryptionAlgorithmsClient2Server;
    private String clientEncryptionAlgorithmsServer2Client;
    private String serverEncryptionAlgorithmsServer2Client;
    private String finalEncryptionAlgorithmsServer2Client;
    private String clientMacAlgorithmsClient2Server;
    private String serverMacAlgorithmsClient2Server;
    private String finalMacAlgorithmsClient2Server;
    private String clientMacAlgorithmsServer2Client;
    private String serverMacAlgorithmsServer2Client;
    private String finalMacAlgorithmsServer2Client;
    private String clientCompressionAlgorithmsClient2Server;
    private String serverCompressionAlgorithmsClient2Server;
    private String finalCompressionAlgorithmsClient2Server;
    private String serverCompressionAlgorithmsServer2Client;
    private String clientCompressionAlgorithmsServer2Client;
    private String finalCompressionAlgorithmsServer2Client;
    private String clientPublicKey;
    private String serverPublicKey;
    private String finalPublicKeyAlgorithms;

}
