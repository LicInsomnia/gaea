package com.tincery.gaea.source.openven.execute;


import com.tincery.gaea.api.base.Handshake;
import com.tincery.gaea.api.src.OpenVpnData;
import com.tincery.gaea.api.src.extension.OpenVpnExtension;
import com.tincery.gaea.api.src.extension.SslCer;
import com.tincery.gaea.core.base.mgt.CommonConst;
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
     *
     *
     * 装填顺序  先去确认isD2SServer  因为基础属性的装填是根据isD2SServer来的
     * 然后装填extension  extension是根据isD2SServer 和前面的S2D D2S相比较的结果出的
     *
     */
    @Override
    public OpenVpnData pack(String line) throws Exception {
        OpenVpnData openVpnData = new OpenVpnData();
        String[] elements = StringUtils.FileLineSplit(line);
        //设置不会因为isD2SServer 变动的数据

        //获得该条数据 关键变量 可能为null  为null的情况下 继续执行其他判断的方法
        Boolean isD2SServer = sureisD2SServer(elements, openVpnData);
        /*以下 根据isD2SServer 书写属性*/
        fixCommon(elements, openVpnData);
        OpenVpnExtension openVpnExtension = fixOtherCommonAndExtension(openVpnData, elements, isD2SServer);
        try {
            openVpnData.setForeign(this.openVpnLineSupport.isForeign(openVpnData.getServerIp()));
        }catch (RuntimeException e){
            openVpnData.setForeign(false);
            log.warn("无法判断ipv6内外网，默认设置为false，数据为{}",line);
        }

        openVpnData.setOpenVpnExtension(openVpnExtension);
        return openVpnData;
    }

    /**
     * 装载其他common数据 和生成extension
     * @param openVpnData 要装载的实体
     * @param elements 源
     * @param isD2SServer 判断依据
     * @return extension
     * @throws Exception 数据解析错误
     */
    private OpenVpnExtension fixOtherCommonAndExtension(OpenVpnData openVpnData, String[] elements, Boolean isD2SServer) throws Exception {
        OpenVpnExtension openVpnExtension = new OpenVpnExtension();
        if (openVpnData.getDataType() != -1) {
            //设置malformed情况下不会因为isD2SServer变动的数据 因为malformed的setMalformedPayload需要先填入端口等数据 所以不能再这里添加
            if (elements[29].contains("malformed")) {
                // 当openVpn dataType!=-1 且 29 有malformed的时候
                openVpnData.setDataType(-2);
            } else {
                /*这里确定不是malformed  装填其他属性
                设置正常(非malformed)情况下 根据isD2SServer变动的基础数据*/
                fixCommonNormal(elements,openVpnData,isD2SServer);
                //设置握手和会话信息 ※
                addOpenVpnExtension(elements, openVpnExtension,isD2SServer);
            }
        }
        if (openVpnData.getDataType()<0){
            //装填malformed 根据isD2SServer变动的基础属性
            fixPropertiesMalformed(elements,openVpnData);
            this.openVpnLineSupport.setMalformedPayload(elements[29], elements[30], openVpnData);
        }
        return openVpnExtension;
    }

    /**
     * 确定第一个能代表 isD2SServer的位置
     */
    private Boolean sureisD2SServer(String[] elements,OpenVpnData openVpnData) throws Exception {
        int dataType = Integer.parseInt(elements[8]);
        this.openVpnLineSupport.fixForJudgeIsServer(openVpnData,dataType,elements[14],elements[15]);
        if (dataType != -1){
            if (elements[29].contains("malformed")) {
                // 当openVpn dataType!=-1 且 29 有malformed的时候
                openVpnData.setDataType(-2);
            }else{
                return this.openVpnLineSupport.judgeisD2SServer(elements, 29, openVpnData);
            }
        }
        return null;
    }

    /**
     * 装填malformed基础属性
     * @param elements 输入的数据
     * @param openVpnData 要装载的数据
     */
    private void fixPropertiesMalformed(String[] elements, OpenVpnData openVpnData) {
        Boolean isD2SServer;
        try {
            isD2SServer = this.openVpnLineSupport.sureisD2SServerByIsInnerIp(elements[12], elements[13], null);
        }catch (Exception e){
            // 这里可能遇到内外网检测方法无法检测ipv6  跳过进行端口检测
            isD2SServer =  this.openVpnLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]),Integer.parseInt(elements[15]));
        }
        if (Objects.isNull(isD2SServer)){
            isD2SServer =  this.openVpnLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]),Integer.parseInt(elements[15]));
        }
        if (isD2SServer){
            fixPropertiesTrue(elements,openVpnData);
        }else {
            fixPropertiesFalse(elements,openVpnData);
        }

    }


    private void fixCommon(String[] elements, OpenVpnData openVpnData) {
        openVpnData.setSource(elements[16]);
        this.openVpnLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), openVpnData);
        openVpnData.setSyn("1".equals(elements[0]));
        openVpnData.setFin("1".equals(elements[1]));
        this.openVpnLineSupport.setMobileElements(elements[18], elements[19], elements[20], openVpnData);
        openVpnData.setDataType(Integer.parseInt(elements[8]));
    }

    /**
     * 根据协议 决定装填顺序
     * isD2SServer false: src = Server dst = Client
     * isD2SServer true： src = Client dst = Server
     */
    private void fixCommonNormal(String[] elements, OpenVpnData openVpnData,Boolean isD2SServer) {

        int protocol = Integer.parseInt(elements[9]);
        if (Objects.equals(CommonConst.TCP,protocol)){
            //TCP协议装填 按照原有逻辑 d2s为client up  s2d为server down fixPropertiesTrue
            fixPropertiesTrue(elements,openVpnData);
        }else if (Objects.equals(CommonConst.UDP,protocol)){
            //UDP协议装填  要在extension之后加载 根据isD2SServer加载
            if (Objects.isNull(isD2SServer)){
                isD2SServer = this.openVpnLineSupport.sureisD2SServerByPortAndDataType(openVpnData.getDataType(), openVpnData.getServerPort(), openVpnData.getClientPort(), isD2SServer);
            }
            if (Objects.isNull(isD2SServer)){
                // srcIp dstIp
                isD2SServer = this.openVpnLineSupport.sureisD2SServerByIsInnerIp(elements[12], elements[13], isD2SServer);
                if (Objects.isNull(isD2SServer)){
                    isD2SServer = this.openVpnLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]), Integer.parseInt(elements[15]));
                }
                openVpnData.setDataType(0);
            }

            if (isD2SServer){
                //isD2SServer = true src = Client dst = Server
                //d2s为server down  s2d为client up
                fixPropertiesTrue(elements,openVpnData);
            }else{
                //isD2SServer = false src = Server dst = Client
                //d2s up  s2d down
                fixPropertiesFalse(elements,openVpnData);
            }
        }
    }

    /**
     * 装载 isD2SServer 为true 的数据（和TCP数据）
     * @param elements 源
     * @param openVpnData 实体
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
     * 装载 isD2SServer 为false 的数据
     * @param elements 源
     * @param openVpnData 实体
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

    private void addOpenVpnExtension(String[] elements, OpenVpnExtension openVpnExtension,Boolean isD2SServer) throws Exception {
        Handshake handshake = null;
        Boolean flag = null;
        for (int i = 29; i < elements.length; i++) {
            if (StringUtils.isEmpty(elements[i])) {
                continue;
            }
            if (elements[i].startsWith("(")) {
                // 为会话属性信息
                addSessionProperties(elements[i], openVpnExtension, flag);
            } else {
                if (Objects.isNull(handshake)){
                    handshake = new Handshake();
                }
                // 为握手会话信息
                flag = addHandshake(elements[i], openVpnExtension, handshake, isD2SServer);
            }
        }
        openVpnExtension.setHandshake(handshake);
    }

    private Boolean changeD2S(String handshakeKeyword,Boolean isD2SServer){
        String substring = handshakeKeyword.substring(0, 3);
        if (Objects.equals(substring,"D2S")){
            isD2SServer = changeisD2SServer(isD2SServer,1);
        }else if (Objects.equals(substring,"S2D")){
            isD2SServer = changeisD2SServer(isD2SServer,0);
        }
        return isD2SServer;
    }

    /**
     * 解析拓展信息中的握手相关会话
     * handShake装载过程 isD2SServer和D2S S2D对比
     * data.startWith("D2S Client")   --> S2D--Server  D2S--Client
     * data.startWith("S2D Client")   --> S2D--Client  D2S--Server
     * data.startWith("D2S Server")	  --> S2D--Client  D2S--Server
     * data.startWith("S2D Server")	  --> S2D--Server  D2S--Client
     * 基础信息 是由第一个S2D （Client/Server）或者 D2S(Client/Server) 决定的
     *
     * @param element   拓展信息
     * @param handshake 握手过程
     */
    private Boolean addHandshake(String element, OpenVpnExtension openVpnExtension, Handshake handshake,Boolean isD2SServer) {
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
        isD2SServer = changeD2S(handshakeKeyword, isD2SServer);
        return isD2SServer;
    }

    /**
     * data.startWith("D2S Client")   --> S2D--Server  D2S--Client
     * data.startWith("S2D Client")   --> S2D--Client  D2S--Server
     * data.startWith("D2S Server")	  --> S2D--Client  D2S--Server
     * data.startWith("S2D Server")	  --> S2D--Server  D2S--Client
     * S2D  为 0
     * D2S  为 1
     * 可见
     *      isD2SServer : false :  S2D Client  D2S Server
     *      true:  D2S Client   S2D Server
     */
    private Boolean changeisD2SServer(Boolean isD2SServer , int s2dOrd2s){
        if (isD2SServer){
            switch (s2dOrd2s){
                case 0:
                    return false;
                case 1:
                    return true;
            }
        }else{
            switch (s2dOrd2s){
                case 0:
                    return true;
                case 1:
                    return false;
            }
        }
        return isD2SServer;
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


    private void addCerChain(String cer, OpenVpnExtension openVpnExtension, boolean isD2SServer) {
        List<SslCer> cerChain;
        if (isD2SServer) {
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
        cerChain.add(new SslCer(cer));
    }

}
