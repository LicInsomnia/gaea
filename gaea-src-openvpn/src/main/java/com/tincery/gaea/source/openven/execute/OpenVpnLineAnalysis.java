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
import java.util.Objects;

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
        //设置不会因为isServer 变动的数据
        setFixProperties(elements, openVpnData);
        OpenVpnExtension openVpnExtension = new OpenVpnExtension();
        if (openVpnData.getDataType() == -1) {
            //设置malformed情况下不会因为isServer变动的数据
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        } else {
            if (elements[29].contains("malformed")) {
                openVpnData.setDataType(-2);
            } else {
                //这里确定不是malformed  然后先过一遍数据 确定isServer  然后再装填其他属性
                Boolean isServer = openVpnIsServer(elements,openVpnData);
                if (Objects.nonNull(isServer)){
                    //设置正常(非malformed)情况下 根据isServer变动的基础数据
                    fixPropertiesByIsServer(elements,openVpnData,isServer);
                    //设置握手和会话信息
                    addOpenVpnExtension(elements, openVpnExtension, openVpnData,isServer);
                }
            }
        }
        if (openVpnData.getDataType()<0){
            //装填malformed 根据isServer变动的基础属性
            fixPropertiesMalformed(elements,openVpnData);
        }
        openVpnData.setForeign(this.openVpnLineSupport.isForeign(openVpnData.getServerIp()));
        openVpnData.setOpenVpnExtension(openVpnExtension);
        return openVpnData;
    }

    /**
     * 装填malformed基础属性
     * @param elements 输入的数据
     * @param openVpnData 要装载的数据
     */
    private void fixPropertiesMalformed(String[] elements, OpenVpnData openVpnData) {
        boolean isServer = getIsServerByPort(Integer.parseInt(elements[14]),Integer.parseInt(elements[15]));
        if (isServer){
            fixPropertiesTrue(elements,openVpnData);
        }else {
            fixPropertiesFalse(elements,openVpnData);
        }

    }

    /**
     * 先过一遍数据判断isServer
     * @param elements 输入数据
     * @return isServer  返回如果是null  则为malformed  如果不是null  则为boolean
     * isServer false: src = Server dst = Client
     * isServer true： src = Client dst = Server
     */
    private Boolean openVpnIsServer(String[] elements,OpenVpnData openVpnData) throws Exception {
        Boolean isServer = false;

        if (openVpnData.getDataType() == -1) {
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
            return null;
        }
        if (elements[29].contains("malformed")) {
            openVpnData.setDataType(-2);
            return null;
        }
        for (int i = 29; i < elements.length; i++) {
            if (StringUtils.isEmpty(elements[i])) {
                continue;
            }
            isServer = sureIsServer(elements[i]);
            if (Objects.nonNull(isServer)){
                break;
            }
        }
        //如果直到这里isServer都没有值 那么通过端口来判断isServer
        if (Objects.isNull(isServer)){
            int srcPort = Integer.parseInt(elements[14]);
            int dstPort = Integer.parseInt(elements[15]);
            // src对应server dst对应client
            // src对应client dst对应server
            isServer = getIsServerByPort(srcPort,dstPort);
        }
        return isServer;
    }
    /**
     * 根据端口判断isServer
     */
    private boolean getIsServerByPort(int srcPort,int dstPort){
        return srcPort > dstPort;
    }

    /**
     * 该方法是确定isServer关键变量的值的。。因为要遍历所有的握手信息才能获得该变量值，然后根据该变量去装填其他属性
     * @param element 根据该元素判断
     * @return isServer
     */
    private Boolean sureIsServer(String element) throws Exception {
        Boolean isServer = null;
        String[] kv = element.split(":");
        if (kv.length != 2) {
            throw new Exception("握手会话数据格式有误...");
        }
        char dOrs = kv[0].charAt(0);
        char cORs = kv[0].charAt(4);
        switch (dOrs){
            case 'D':
                if (Objects.equals(cORs,'C')){
                    // 例如：D2S Client
                    isServer = false;
                }else if (Objects.equals(cORs,'S')){
                    // 例如：D2S Server Hello
                    isServer = true;
                }
                break;
            case 'S':
                if (Objects.equals(cORs,'C')){
                    // 例如S2D Client Hello
                    isServer = true;
                }else if (Objects.equals(cORs,'S')){
                    // 例如S2D Server Hello
                    isServer = false;
                }
                break;
        }
        return isServer;
    }

    private void setFixProperties(String[] elements, OpenVpnData openVpnData) {
        openVpnData.setSource(elements[16]);
        this.openVpnLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), openVpnData);
        openVpnData.setDataType(Integer.parseInt(elements[8]));
        openVpnData.setSyn("1".equals(elements[0]));
        openVpnData.setFin("1".equals(elements[1]));
        this.openVpnLineSupport.setMobileElements(elements[18], elements[19], elements[20], openVpnData);

    }

    /**
     * 根据协议 决定装填顺序
     * isServer false: src = Server dst = Client
     * isServer true： src = Client dst = Server
     */
    private void fixPropertiesByIsServer(String[] elements, OpenVpnData openVpnData,Boolean isServer) {

        int protocol = Integer.parseInt(elements[9]);
        if (Objects.equals(6,protocol)){
            //TCP协议装填 按照原有逻辑 d2s为client up  s2d为server down
            fixPropertiesTrue(elements,openVpnData);
        }else if (Objects.equals(17,protocol)){
            //UDP协议装填  要在extension之后加载 根据isServer加载

            if (isServer){
                //isServer = true src = Client dst = Server
                //d2s为server down  s2d为client up
                fixPropertiesTrue(elements,openVpnData);
            }else{
                //isServer = false src = Server dst = Client
                //d2s up  s2d down
                //装载逻辑和TCP一致
                fixPropertiesFalse(elements,openVpnData);
            }
        }
    }

    /**
     * 装载 isServer 为true 的数据（和TCP数据）
     * @param elements
     * @param openVpnData
     */
    private void fixPropertiesTrue(String[] elements,OpenVpnData openVpnData){
        this.openVpnLineSupport.set7Tuple(
                elements[11],elements[10],elements[13],elements[12],
                elements[15],elements[14],elements[9],HeadConst.PRONAME.OPENVPN,openVpnData);
        this.openVpnLineSupport.setFlow(
                elements[6], elements[7], elements[4],
                elements[5], openVpnData);
        this.openVpnLineSupport.setTargetName(elements[17], openVpnData);
        this.openVpnLineSupport.setGroupName(openVpnData);
        this.openVpnLineSupport.set5TupleOuter( elements[22],elements[21], elements[24],  elements[23],elements[25], openVpnData);
        this.openVpnLineSupport.setPartiesId( elements[27], elements[26],openVpnData);
    }
    /**
     * 装载 isServer 为false 的数据
     * @param elements
     * @param openVpnData
     */
    private void fixPropertiesFalse(String[] elements,OpenVpnData openVpnData){
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
        this.openVpnLineSupport.setPartiesId(elements[26], elements[27], openVpnData);
    }

    private void addOpenVpnExtension(String[] elements, OpenVpnExtension openVpnExtension, OpenVpnData openVpnData,Boolean isServer) throws Exception {
        if (openVpnData.getDataType() == -1) {
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        } else {
            if (elements[29].contains("malformed")) {
                openVpnData.setDataType(-2);
            } else {
                Handshake handshake = null;
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
                            addHandshake(elements[i], openVpnExtension, handshake,elements);
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
     * @param elements 输入数据
     */
    private void addHandshake(String element, OpenVpnExtension openVpnExtension, Handshake handshake,String[] elements) throws Exception {
        String[] kv = element.split(":");
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
                openVpnExtension.setHasApplicationData(true);
                break;
            default:
                break;
        }
    }

    private void addSessionProperties(String element, OpenVpnExtension openVpnExtension, Boolean isServer) throws Exception {
        if (null == isServer) {
            throw new Exception("握手会话数据格式有误...在确定是否是服务端之前遇到会话信息");
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
