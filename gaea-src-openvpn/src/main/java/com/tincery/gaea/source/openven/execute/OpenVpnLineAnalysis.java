package com.tincery.gaea.source.openven.execute;


import com.tincery.gaea.api.base.Handshake;
import com.tincery.gaea.api.src.OpenVpnData;
import com.tincery.gaea.api.src.extension.OpenVpnExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
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
public class OpenVpnLineAnalysis implements SrcLineAnalysis<OpenVpnData> {

    @Autowired
    private OpenVpnLineSupport openVpnLineSupport;

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
    public OpenVpnData pack(String line) throws Exception {
        OpenVpnData openVpnData = new OpenVpnData();
        String[] elements = StringUtils.FileLineSplit(line);
        setFixProperties(elements, openVpnData);
        OpenVpnExtension openVpnExtension = new OpenVpnExtension();
        if (openVpnData.getDataType() == -1) {
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        } else {
            if (elements[29].contains("malformed")) {
                openVpnData.setDataType(-2);
            } else {
                addSslExtension(elements, openVpnExtension, openVpnData);
            }
        }
        openVpnData.setSslExtension(openVpnExtension);
        return openVpnData;
    }

    private void setFixProperties(String[] elements, OpenVpnData openVpnData) {
        openVpnData.setSource(elements[16]);
        this.openVpnLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), openVpnData);
        openVpnData.setDataType(Integer.parseInt(elements[8]));
        openVpnData.setSyn("1".equals(elements[0]));
        openVpnData.setFin("1".equals(elements[1]));
        this.openVpnLineSupport.set7Tuple(
                elements[10], elements[11], elements[12],
                elements[13], elements[14], elements[15],
                elements[9], HeadConst.PRONAME.OPENVPN, openVpnData);
        this.openVpnLineSupport.setFlow(
                elements[4], elements[5], elements[6],
                elements[7], openVpnData);
        this.openVpnLineSupport.setTargetName(elements[17], openVpnData);
        this.openVpnLineSupport.setGroupName(openVpnData);
        this.openVpnLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], openVpnData);
        this.openVpnLineSupport.setMobileElements(elements[18], elements[19], elements[20], openVpnData);
        this.openVpnLineSupport.setPartiesId(elements[26], elements[27], openVpnData);
        openVpnData.setForeign(this.openVpnLineSupport.isForeign(openVpnData.getServerIp()));
    }

    private void addSslExtension(String[] elements, OpenVpnExtension openVpnExtension, OpenVpnData openVpnData) throws Exception {
        if (openVpnData.getDataType() == -1) {
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        } else {
            if (elements[29].contains("malformed")) {
                openVpnData.setDataType(-2);
            } else {
                Handshake handshake = null;
                Boolean isServer = null;
                for (int i = 29; i < elements.length; i++) {
                    if (StringUtils.isEmpty(elements[i])) {
                        continue;
                    }
                    if (elements[i].startsWith("(")) {
                        // 为会话属性信息
                        addSessionProperties(elements[i], openVpnExtension, isServer);
                    } else {
                        if (null == handshake) {
                            handshake = new Handshake();
                        }
                        // 为握手会话信息
                        isServer = addHandshake(elements[i], openVpnExtension, handshake);
                    }
                }
                openVpnExtension.setHandshake(handshake);
            }
        }
    }

    /**
     * 解析拓展信息中的握手相关会话
     *
     * @param element   拓展信息
     * @param handshake 握手过程
     * @return 是否进行客户端服务端切换
     */
    private boolean addHandshake(String element, OpenVpnExtension openVpnExtension, Handshake handshake) throws Exception {
        String[] kv = element.split(":");
        if (kv.length != 2) {
            throw new Exception("握手会话数据格式有误...");
        }
        boolean isServer;
        switch (kv[0].charAt(0)) {
            case 'C':
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
            case "C Client Hello":
                handshake.setClientHello(length);
                break;
            case "S Server Hello":
                handshake.setServerHello(length);
                break;
            case "S Certificate":
                handshake.setServerCertificate(length);
                break;
            case "S Server Key Exchange":
                handshake.setServerKeyExchange(length);
                break;
            case "S Certificate Request":
                handshake.setServerCertificateRequest(length);
                break;
            case "S Server Hello Done":
                handshake.setServerHelloDone(length);
                break;
            case "C Certificate":
                handshake.setClientCertificate(length);
                break;
            case "C Client Key Exchange":
                handshake.setClientKeyExchange(length);
                break;
            case "C Certificate Verify":
                handshake.setClientCertificateVerify(length);
                break;
            case "C Finished":
                handshake.setClientFinished(length);
                break;
            case "S Finished":
                handshake.setServerFinished(length);
                break;
            case "C Change Cipher Spec":
                handshake.setClientChangeCipherSpec(length);
                break;
            case "S Change Cipher Spec":
                handshake.setServerChangeCipherSpec(length);
                break;
            case "C Application Data":
            case "S Application Data":
                openVpnExtension.setHasApplicationData(true);
                break;
            default:
                break;
        }
        return isServer;
    }

    private void addSessionProperties(String element, OpenVpnExtension openVpnExtension, Boolean isServer) throws Exception {
        if (null == isServer) {
            throw new Exception("握手会话数据格式有误...");
        }
        String[] kv = element.substring(1, element.length() - 1).split(":");
        if (kv.length != 2) {
            throw new Exception("握手会话数据格式有误...");
        }
        String key = kv[0].trim();
        String value = kv[1].trim();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            throw new Exception("握手会话数据格式有误...");
        }
        switch (key) {
            case "fingerprint":
                if (isServer) {
                    openVpnExtension.setServerFingerPrint(value);
                } else {
                    openVpnExtension.setClientFingerPrint(value);
                }
                break;
            case "JA3":
                openVpnExtension.setClientJA3(value);
                break;
            case "CipherSuite":
                openVpnExtension.setClientCipherSuites(value);
                break;
            case "ServerName":
                openVpnExtension.setServerName(value);
                break;
            case "ssl_version":
                openVpnExtension.setVersion(value);
                break;
            case "cipher_suite":
                openVpnExtension.setCipherSuite(this.openVpnLineSupport.getCipherSuite(value));
                break;
            case "HashAlgorithms":
                openVpnExtension.setClientHashAlgorithms(value);
                break;
            case "JA3S":
                openVpnExtension.setServerJA3(value);
                break;
            case "named_curve":
                openVpnExtension.setServerECDHNamedCurve(value);
                break;
            case "publicKey data":
                openVpnExtension.setServerECDHPublicKeyData(value);
                break;
            case "signature algorithm":
                openVpnExtension.setServerECDHSignatureAlgorithm(value);
                break;
            case "signature data":
                openVpnExtension.setServerECDHSignatureData(value);
                break;
            case "cert":
                addCerChain(value, openVpnExtension, isServer);
                break;
            default:
                break;
        }
    }

    private void addCerChain(String cer, OpenVpnExtension openVpnExtension, boolean isServer) {
        List<String> cerChain;
        if (isServer) {
            cerChain = openVpnExtension.getServerCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                openVpnExtension.setServerCerChain(cerChain);
            }
        } else {
            cerChain = openVpnExtension.getClientCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                openVpnExtension.setClientCerChain(cerChain);
            }
        }
        cerChain.add(cer);
    }

}
