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

    public void merge(Handshake that) {
        if (Objects.isNull(that)) {
            return;
        }
        if (that.clientHello > 0) {
            this.clientHello = that.clientHello;
        }
        if (that.serverHello > 0) {
            this.serverHello = that.serverHello;
        }
        if (that.serverCertificate > 0) {
            this.serverCertificate = that.serverCertificate;
        }
        if (that.serverKeyExchange > 0) {
            this.serverKeyExchange = that.serverKeyExchange;
        }
        if (that.serverCertificateRequest > 0) {
            this.serverCertificateRequest = that.serverCertificateRequest;
        }
        if (that.serverHelloDone > 0) {
            this.serverHelloDone = that.serverHelloDone;
        }
        if (that.clientCertificate > 0) {
            this.clientCertificate = that.clientCertificate;
        }
        if (that.clientKeyExchange > 0) {
            this.clientKeyExchange = that.clientKeyExchange;
        }
        if (that.clientCertificateVerify > 0) {
            this.clientCertificateVerify = that.clientCertificateVerify;
        }
        if (that.clientFinished > 0) {
            this.clientFinished = that.clientFinished;
        }
        if (that.serverFinished > 0) {
            this.serverFinished = that.serverFinished;
        }
        if (that.clientChangeCipherSpec > 0) {
            this.clientChangeCipherSpec = that.clientChangeCipherSpec;
        }
        if (that.serverChangeCipherSpec > 0) {
            this.serverChangeCipherSpec = that.serverChangeCipherSpec;
        }
    }
}
