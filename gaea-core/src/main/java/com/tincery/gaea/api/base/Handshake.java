package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Handshake {

    private int clientHello;
    private int serverHello;
    private int serverCertificate;
    private int serverKeyExchange;
    private int serverCertificateRequest;
    private int serverHelloDone;
    private int clientCertificate;
    private int clientKeyExchange;
    private int clientCertificateVerify;
    private int clientFinished;
    private int serverFinished;
    private int clientChangeCipherSpec;
    private int serverChangeCipherSpec;

    public Handshake() {
        this.clientHello = this.serverHello = this.serverCertificate = this.serverKeyExchange =
                this.serverCertificateRequest = this.serverHelloDone = this.clientCertificate =
                        this.clientKeyExchange = this.clientCertificateVerify = this.clientFinished =
                                this.serverFinished = this.clientChangeCipherSpec = this.serverChangeCipherSpec = -1;
    }
}
