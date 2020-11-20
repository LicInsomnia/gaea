package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    public void merge(Handshake handshake) {
        if (Objects.isNull(handshake)){
            return;
        }
        if (handshake.clientHello > 0) {
            this.clientHello = handshake.clientHello;
        }
        if (handshake.serverHello > 0) {
            this.serverHello = handshake.serverHello;
        }
        if (handshake.serverCertificate > 0) {
            this.serverCertificate = handshake.serverCertificate;
        }
        if (handshake.serverKeyExchange > 0) {
            this.serverKeyExchange = handshake.serverKeyExchange;
        }
        if (handshake.serverCertificateRequest > 0) {
            this.serverCertificateRequest = handshake.serverCertificateRequest;
        }
        if (handshake.serverHelloDone > 0) {
            this.serverHelloDone = handshake.serverHelloDone;
        }
        if (handshake.clientCertificate > 0) {
            this.clientCertificate = handshake.clientCertificate;
        }
        if (handshake.clientKeyExchange > 0) {
            this.clientKeyExchange = handshake.clientKeyExchange;
        }
        if (handshake.clientCertificateVerify > 0) {
            this.clientCertificateVerify = handshake.clientCertificateVerify;
        }
        if (handshake.clientFinished > 0) {
            this.clientFinished = handshake.clientFinished;
        }
        if (handshake.serverFinished > 0) {
            this.serverFinished = handshake.serverFinished;
        }
        if (handshake.clientChangeCipherSpec > 0) {
            this.clientChangeCipherSpec = handshake.clientChangeCipherSpec;
        }
        if (handshake.serverChangeCipherSpec > 0) {
            this.serverChangeCipherSpec = handshake.serverChangeCipherSpec;
        }
    }
}
