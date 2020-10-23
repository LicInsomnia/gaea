package com.tincery.gaea.api.base;

public class SslCipherSuite {

    private String proname;
    private String version;
    private String handshake;
    private CipherSuiteDO cipherSuite;
    private String serverCerChain;

    @Override
    public String toString() {
        return proname + '_' + version + '_' + handshake + '_' + cipherSuite.getId() + '_' + serverCerChain;
    }
}
