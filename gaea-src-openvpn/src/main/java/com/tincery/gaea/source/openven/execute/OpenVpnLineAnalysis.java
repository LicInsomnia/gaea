package com.tincery.gaea.source.openven.execute;


import com.tincery.gaea.api.src.OpenVpnData;
import com.tincery.gaea.core.base.mgt.HeadConst;
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
    public OpenVpnData pack(String line) {
        OpenVpnData openVpnData = new OpenVpnData();
        String[] elements = StringUtils.FileLineSplit(line);
        openVpnData.setSource(elements[16]);
        openVpnData.setCapTime(DateUtils.validateTime(Long.parseUnsignedLong(elements[2])));
        openVpnData.setDuration((Long.parseUnsignedLong(elements[3])) - Long.parseUnsignedLong(elements[2]));
        openVpnData.setImsi(elements[18]);
        openVpnData.setImei(elements[19]);
        openVpnData.setMsisdn(elements[20]);
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
        openVpnData.setUserId(elements[26]);
        openVpnData.setServerId(elements[27]);
        if (openVpnData.getDataType() == -1) {
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        } else {
            if (elements[29].contains("malformed")) {
                openVpnData.setDataType(-2);
            } else {
                addOpenVpnExtension(elements, openVpnData);
            }
        }
        return openVpnData;
    }

    private void addOpenVpnExtension(String[] elements, OpenVpnData openVpnData) {
        List<String> handshake = new ArrayList<>();
        boolean isServer = false;
        for (int i = 29; i < elements.length; i++) {
            if (StringUtils.isEmpty(elements[i])) {
                continue;
            }
            if (elements[i].startsWith("(")) {
                // 为会话属性信息
                addSessionProperties(elements[i], openVpnData, isServer);
            } else {
                // 为握手会话信息
                isServer = isServer || addHandshake(elements[i], openVpnData, handshake);
            }
        }
        openVpnData.setHandshake(handshake);
    }

    /**
     * 解析拓展信息中的握手相关会话
     *
     * @param element   拓展信息
     * @param handshake 握手过程
     * @return 是否进行客户端服务端切换
     */
    private boolean addHandshake(String element, OpenVpnData openVpnData, List<String> handshake) {
        boolean isServer = false;
        String[] kv = element.split(":");
        if (kv.length != 2) {
            return false;
        }
        String handshakeKeyword = kv[0].trim();
        if ("Application Data".equals(handshakeKeyword)) {
            openVpnData.setHasApplicationData(true);
            return false;
        }
        if ("Server Hello".equals(handshakeKeyword)) {
            isServer = true;
        }
        handshake.add(handshakeKeyword);
        return isServer;
    }

    private void addSessionProperties(String element, OpenVpnData openVpnData, boolean isServer) {
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
                    openVpnData.setServerFingerPrint(value);
                } else {
                    openVpnData.setClientFingerPrint(value);
                }
                break;
            case "JA3":
                openVpnData.setClientJA3(value);
                break;
            case "CipherSuite":
                openVpnData.setClientCipherSuites(value);
                break;
            case "ServerName":
                openVpnData.setServerName(value);
                break;
            case "ssl_version":
                openVpnData.setVersion(value);
                break;
            case "cipher_suite":
                openVpnData.setCipherSuite(this.openVpnLineSupport.getCipherSuite(value));
                break;
            case "HashAlgorithms":
                openVpnData.setClientHashAlgorithms(value);
                break;
            case "JA3S":
                openVpnData.setServerJA3(value);
                break;
            case "named_curve":
                openVpnData.setServerECDHNamedCurve(value);
                break;
            case "publicKey data":
                openVpnData.setServerECDHPublicKeyData(value);
                break;
            case "signature algorithm":
                openVpnData.setServerECDHSignatureAlgorithm(value);
                break;
            case "signature data":
                openVpnData.setServerECDHSignatureData(value);
                break;
            case "cert":
                addCerchain(value, openVpnData, isServer);
                break;
            default:
                break;
        }
    }

    private void addCerchain(String cer, OpenVpnData openVpnData, boolean isServer) {
        List<String> cerChain;
        if (isServer) {
            cerChain = openVpnData.getServerCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                openVpnData.setServerCerChain(cerChain);
            }
        } else {
            cerChain = openVpnData.getClientCerChain();
            if (null == cerChain) {
                cerChain = new ArrayList<>();
                openVpnData.setClientCerChain(cerChain);
            }
        }
        cerChain.add(cer);
    }

}
