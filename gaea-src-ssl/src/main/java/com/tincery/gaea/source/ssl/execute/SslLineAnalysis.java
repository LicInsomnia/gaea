package com.tincery.gaea.source.ssl.execute;


import com.tincery.gaea.api.src.SslData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gxz
 */

@Component
@Slf4j
public class SslLineAnalysis implements SrcLineAnalysis<SslData> {

    @Autowired
    private SslLineSupport sslLineSupport;

    /**
     * 0.syn            1.fin           2.startTime         3.endTime           4.uppkt
     * 5.upbyte         6.downpkt       7.downbyte          8.datatype(1)       9.protocol
     * 10.serverMac     11.clientMac    12.serverIp_n       13.clientIp_n       14.serverPort
     * 15.clientPort    16.source       17.runleName        18.imsi             19.imei
     * 20.msisdn        21.outclientip  22.outserverip      23.outclientport    24.outserverport
     * 25.outproto      26.userid       27.serverid         28.ismac2outer      29.data1 / upPayload
     * 30.data2 / downPayload           ...dataN
     */
    @Override
    public SslData pack(String line) {
        SslData sslData = new SslData();
        String[] elements = StringUtils.FileLineSplit(line);
        sslData.setSource(elements[16]);
        sslData.setCapTime(DateUtils.validateTime(Long.parseUnsignedLong(elements[2])));
        sslData.setDuration((Long.parseUnsignedLong(elements[3])) - Long.parseUnsignedLong(elements[2]));
        sslData.setImsi(elements[18]);
        sslData.setImei(elements[19]);
        sslData.setMsisdn(elements[20]);
        sslData.setDataType(Integer.parseInt(elements[8]));
        sslData.setSyn("1".equals(elements[0]));
        sslData.setFin("1".equals(elements[1]));
        this.sslLineSupport.set7Tuple(
                elements[10], elements[11], elements[12],
                elements[13], elements[14], elements[15],
                elements[9], "SSL", sslData);
        this.sslLineSupport.setFlow(
                elements[4], elements[5], elements[6],
                elements[7], sslData);
        this.sslLineSupport.setTargetName(elements[17], sslData);
        this.sslLineSupport.setGroupName(sslData);
        this.sslLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], sslData);
        sslData.setUserId(elements[26]);
        sslData.setServerId(elements[27]);
        if (sslData.getDataType() == -1) {
            this.sslLineSupport.setMalformedPayload(elements[29], elements[30], sslData);
        } else {
            if (elements[29].contains("malformed")) {
                sslData.setDataType(-2);
            } else {
                addSslExtension(elements, sslData);
            }
        }
        return sslData;
    }

    private void addSslExtension(String[] elements, SslData sslData) {
        List<String> handshake = new ArrayList<>();
        boolean isServer = false;
        for (int i = 29; i < elements.length; i++) {
            if (StringUtils.isEmpty(elements[i])) {
                continue;
            }
            if (elements[i].startsWith("(")) {
                // 为会话属性信息
                addSessionProperties(elements[i], sslData, isServer);
            } else {
                // 为握手会话信息
                isServer = isServer || addHandshake(elements[i], sslData, handshake);
            }
        }
        sslData.setHandshake(handshake);
    }

    /**
     * 解析拓展信息中的握手相关会话
     *
     * @param element   拓展信息
     * @param handshake 握手过程
     * @return 是否进行客户端服务端切换
     */
    private boolean addHandshake(String element, SslData sslData, List<String> handshake) {
        boolean isServer = false;
        String[] kv = element.split(":");
        if (kv.length != 2) {
            return false;
        }
        String handshakeKeyword = kv[0].trim();
        if ("Application Data".equals(handshakeKeyword)) {
            sslData.setHasApplicationData(true);
            return false;
        }
        if ("Server Hello".equals(handshakeKeyword)) {
            isServer = true;
        }
        handshake.add(handshakeKeyword);
        return isServer;
    }

    private void addSessionProperties(String element, SslData sslData, boolean isServer) {
        String[] kv = element.substring(1, element.length() - 1).split(":");
        if (kv.length != 2) {
            return;
        }
        String key = kv[0].trim();
        String value = kv[1].trim();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        switch (key) {
            case "fingerprint":
                if (isServer) {
                    sslData.setServerFingerPrint(value);
                } else {
                    sslData.setClientFingerPrint(value);
                }
                break;
            case "JA3":
                sslData.setClientJA3(value);
                break;
            case "CipherSuite":
                sslData.setClientCipherSuites(value);
                break;
            case "ServerName":
                sslData.setServerName(value);
                break;
            case "ssl_version":
                sslData.setVersion(value);
                break;
            case "cipher_suite":
                sslData.setCipherSuite(this.sslLineSupport.getCipherSuite(value));
                break;
            case "HashAlgorithms":
                sslData.setClientHashAlgorithms(value);
                break;
            case "JA3S":
                sslData.setServerJA3(value);
                break;
            case "named_curve":
                sslData.setServerECDHNamedCurve(value);
                break;
            case "publicKey data":
                sslData.setServerECDHPublicKeyData(value);
                break;
            case "signature algorithm":
                sslData.setServerECDHSignatureAlgorithm(value);
                break;
            case "signature data":
                sslData.setServerECDHSignatureData(value);
                break;
            case "cert":
                addCerchain(value, sslData, isServer);
                break;
            default:
                break;
        }
    }

    private void addCerchain(String cer, SslData sslData, boolean isServer) {
        List<String> cerChain;
        if (isServer) {
            cerChain = sslData.getServerCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                sslData.setServerCerChain(cerChain);
            }
        } else {
            cerChain = sslData.getClientCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                sslData.setClientCerChain(cerChain);
            }
        }
        cerChain.add(cer);
    }

}
