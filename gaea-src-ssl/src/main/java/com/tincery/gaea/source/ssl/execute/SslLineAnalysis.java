package com.tincery.gaea.source.ssl.execute;


import com.tincery.gaea.api.base.Handshake;
import com.tincery.gaea.api.src.SslData;
import com.tincery.gaea.api.src.extension.SslExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public SslData pack(String line) throws Exception {
        SslData sslData = new SslData();
        String[] elements = StringUtils.FileLineSplit(line);
        setFixProperties(elements, sslData);
        SslExtension sslExtension = new SslExtension();
        if (sslData.getDataType() == -1) {
            this.sslLineSupport.setMalformedPayload(elements[29], elements[30], sslData);
            return sslData;
        } else {
            if (elements[29].contains("malformed")) {
                sslData.setDataType(-2);
            } else {
                addSslExtension(elements, sslExtension, sslData);
            }
        }
        sslData.setSslExtension(sslExtension);
        return sslData;
    }

    private void setFixProperties(String[] elements, SslData sslData) {
        sslData.setSource(elements[16]);
        this.sslLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), sslData);
        sslData.setDataType(Integer.parseInt(elements[8]));
        sslData.setSyn("1".equals(elements[0]));
        sslData.setFin("1".equals(elements[1]));
        this.sslLineSupport.set7Tuple(
                elements[10], elements[11], elements[12],
                elements[13], elements[14], elements[15],
                elements[9], HeadConst.PRONAME.SSL, sslData);
        this.sslLineSupport.setFlow(
                elements[4], elements[5], elements[6],
                elements[7], sslData);
        this.sslLineSupport.setTargetName(elements[17], sslData);
        this.sslLineSupport.setGroupName(sslData);
        this.sslLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], sslData);
        this.sslLineSupport.setMobileElements(elements[18], elements[19], elements[20], sslData);
        this.sslLineSupport.setPartiesId(elements[26], elements[27], sslData);
        sslData.setForeign(this.sslLineSupport.isForeign(sslData.getServerIp()));
    }

    private void addSslExtension(String[] elements, SslExtension sslExtension, SslData sslData) throws Exception {
        if (sslData.getDataType() == -1) {
            this.sslLineSupport.setMalformedPayload(elements[29], elements[30], sslData);
        } else {
            if (elements[29].contains("malformed")) {
                sslData.setDataType(-2);
            } else {
                Handshake handshake = null;
                Boolean isServer = null;
                for (int i = 29; i < elements.length; i++) {
                    if (StringUtils.isEmpty(elements[i])) {
                        continue;
                    }
                    if (elements[i].startsWith("(")) {
                        // 为会话属性信息
                        addSessionProperties(elements[i], sslExtension, isServer);
                    } else {
                        if (null == handshake) {
                            handshake = new Handshake();
                        }
                        // 为握手会话信息
                        isServer = addHandshake(elements[i], sslExtension, handshake);
                    }
                }
                sslExtension.setHandshake(handshake);
            }
        }
    }


    /**
     * 解析拓展信息中的握手相关会话
     *
     * @param elements  拓展信息
     * @param handshake 握手过程
     * @return 是否进行客户端服务端切换
     */
    private boolean addHandshake(String elements, SslExtension sslExtension, Handshake handshake) throws Exception {
        String[] kv = elements.split(":");
        if (kv.length != 2) {
            throw new Exception("握手会话数据格式有误...");
        }
        boolean isServer;
        char dOrs = kv[0].charAt(0);
        switch (dOrs){
            case 'D':
                isServer = false;
                break;
            case 'S':
                isServer = true;
                break;
            default:
                throw new Exception("握手会话数据格式有误...");
        }

        String handshakeKeyword = kv[0].trim();
        int length = Integer.parseInt(kv[1].trim());
        switch (handshakeKeyword) {
            case "D2S Client Hello":
                handshake.setClientHello(length);
                break;
            case "S2D Server Hello":
                handshake.setServerHello(length);
                break;
            case "S2D Certificate":
                handshake.setServerCertificate(length);
                break;
            case "S2D Server Key Exchange":
                handshake.setServerKeyExchange(length);
                break;
            case "S2D Certificate Request":
                handshake.setServerCertificateRequest(length);
                break;
            case "S2D Server Hello Done":
                handshake.setServerHelloDone(length);
                break;
            case "D2S Certificate":
                handshake.setClientCertificate(length);
                break;
            case "D2S Client Key Exchange":
                handshake.setClientKeyExchange(length);
                break;
            case "D2S Certificate Verify":
                handshake.setClientCertificateVerify(length);
                break;
            case "D2S Finished":
                handshake.setClientFinished(length);
                break;
            case "S2D Finished":
                handshake.setServerFinished(length);
                break;
            case "D2S Change Cipher Spec":
                handshake.setClientChangeCipherSpec(length);
                break;
            case "S2D Change Cipher Spec":
                handshake.setServerChangeCipherSpec(length);
                break;
            case "D2S Application Data":
            case "S2D Application Data":
                sslExtension.setHasApplicationData(true);
                break;
            default:
                break;
        }
        return isServer;
    }

    private void addSessionProperties(String element, SslExtension sslExtension, Boolean isServer) throws Exception {
        if (null == isServer) {
            throw new Exception("握手会话数据格式有误...");
        }
        String[] kv = element.substring(1, element.length() - 1).split(":");
        if (kv.length != 2) {
            return;
        }
        String key = kv[0].trim();
        String value = kv[1].trim();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            throw new Exception("握手会话数据格式有误...");
        }
        switch (key) {
            case "fingerprint":
                if (isServer) {
                    sslExtension.setServerFingerPrint(value);
                } else {
                    sslExtension.setClientFingerPrint(value);
                }
                break;
            case "JA3":
                sslExtension.setClientJA3(value);
                break;
            case "CipherSuite":
                sslExtension.setClientCipherSuites(value);
                break;
            case "ServerName":
                sslExtension.setServerName(value);
                break;
            case "ssl_version":
                sslExtension.setVersion(value);
                break;
            case "cipher_suite":
                sslExtension.setCipherSuite(this.sslLineSupport.getCipherSuite(value));
                break;
            case "HashAlgorithms":
                sslExtension.setClientHashAlgorithms(value);
                break;
            case "JA3S":
                sslExtension.setServerJA3(value);
                break;
            case "named_curve":
                sslExtension.setServerECDHNamedCurve(value);
                break;
            case "publicKey data":
                sslExtension.setServerECDHPublicKeyData(value);
                break;
            case "signature algorithm":
                sslExtension.setServerECDHSignatureAlgorithm(value);
                break;
            case "signature data":
                sslExtension.setServerECDHSignatureData(value);
                break;
            case "cert":
                addCerChain(value, sslExtension, isServer);
                break;
            default:
                break;
        }
    }

    private void addCerChain(String cer, SslExtension sslExtension, boolean isServer) {
        List<String> cerChain;
        if (isServer) {
            cerChain = sslExtension.getServerCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                sslExtension.setServerCerChain(cerChain);
            }
        } else {
            cerChain = sslExtension.getClientCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                sslExtension.setClientCerChain(cerChain);
            }
        }
        cerChain.add(cer);
    }

}
