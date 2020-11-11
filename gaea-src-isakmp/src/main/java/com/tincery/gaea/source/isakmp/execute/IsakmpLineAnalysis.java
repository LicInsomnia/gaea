package com.tincery.gaea.source.isakmp.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.IsakmpData;
import com.tincery.gaea.api.src.extension.IsakmpExtension;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author gongxuanzhang
 */
@Component
public class IsakmpLineAnalysis implements SrcLineAnalysis<IsakmpData> {

    @Autowired
    private IsakmpLineSupport isakmpLineSupport;

    /***
     * 0.syn    1.fin   2.startTime 3.endTime   4.uppkt 5.upbyte    6.downpkt   7.downbyte
     * 8.datatype(-1)   9.protocol  10.serverMac    11.clientMac    12.serverIp_n   13.clientIp_n
     * 14.serverPort   15.clientPort    16.source   17.ruleName 18.imsi 19.imei 20.msisdn
     * 21.outclientip   22.outserverip  23.outclientport    24.outserverport    25.outproto
     * 26.userid    27.serverid 28.ismac2outer  29.upPayload    30.downPayload
     *
     * data1: ~ datan:
     * 装填逻辑：
     * data1 == D2S|S2D  data2:Is_first:1  (第一种逻辑)
     * if element[30]位置 结尾是0  那么装载common
     **/
    @Override
    public IsakmpData pack(String line) throws Exception {
        IsakmpData isakmpData = new IsakmpData();
        String[] elements = StringUtils.FileLineSplit(line);
        Boolean s2dFlag = null;
        isakmpData.setDataType(Integer.parseInt(elements[8]));
        setFixProperties(elements, isakmpData);
        if ("D2S".equals(elements[29])) {
            s2dFlag = false;
        }else if (Objects.equals("S2D",elements[29])){
            s2dFlag = true;
        }
        if (!elements[30].startsWith("Is_first") && isakmpData.getDataType() != -1) {
            throw new Exception("Is_first字段标记错误");
        }
        //TODO 调换这个的位置

        if (isakmpData.getDataType() == -1){
            s2dFlag = getS2DFlag(s2dFlag,elements);
            fixCommonByS2DFlag(isakmpData,elements,s2dFlag);
            isakmpData.setIsakmpExtension(new IsakmpExtension());
            return isakmpData;
        }

        if (elements[30].endsWith("0")) {
            /*使用第二种判断方式 装填common后直接返回*/
            s2dFlag = getS2DFlag(s2dFlag,elements);
            fixCommonByS2DFlag(isakmpData,elements,s2dFlag);
            isakmpData.setIsakmpExtension(new IsakmpExtension());
            isakmpData.setDataType(-2);
            return isakmpData;
        }
        /*根据s2dFlag 判断用什么装填方式*/
        fixCommonByS2DFlag(isakmpData,elements,s2dFlag);
        IsakmpExtension isakmpExtension = new IsakmpExtension();
        int version = Integer.parseInt(elements[33].split(":")[1].trim());
        switch (version) {
            case 1:
                setVersion1(elements, isakmpExtension);
                break;
            case 2:
                setVersion2(elements, isakmpExtension);
                break;
            default:
                throw new Exception("Version版本标记错误");
        }
        isakmpExtension.setExtension();
        isakmpData.setIsakmpExtension(isakmpExtension);
        return isakmpData;
    }

    private Boolean getS2DFlag(Boolean s2dFlag, String[] elements) {
        if (Objects.nonNull(s2dFlag)){
            return s2dFlag;
        }
        s2dFlag = secondTypeS2DFlag(elements,Integer.parseInt(elements[8]));
        if (Objects.nonNull(s2dFlag)){
            return s2dFlag;
        }
        try {
            s2dFlag = this.isakmpLineSupport.sureisD2SServerByIsInnerIp(elements[12], elements[13], null);
        }catch (Exception e){
            s2dFlag = this.isakmpLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]), Integer.parseInt(elements[15]));
        }
        if (Objects.isNull(s2dFlag)){
            s2dFlag = this.isakmpLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]), Integer.parseInt(elements[15]));
        }
        return s2dFlag;
    }

    private Boolean secondTypeS2DFlag(String[] elements,Integer dataType){
        if (dataType == -1){
            return null;
        }
        int srcPort = Integer.parseInt(elements[14]);
        int dstPort = Integer.parseInt(elements[15]);
        if (srcPort == 500 && dstPort != 500){
            return false;
        }else if (srcPort != 500 && dstPort == 500){
            return true;
        }else if (srcPort == 4500 && dstPort != 4500){
            return false;
        }else if (srcPort != 4500 && dstPort == 4500){
            return true;
        }
        return null;
    }

    private void fixCommonByS2DFlag(IsakmpData isakmpData,String[] elements,Boolean s2dFlag){
        this.isakmpLineSupport.set7TupleAndFlow(s2dFlag, elements[10], elements[11], elements[12], elements[13],
                elements[14], elements[15], elements[4], elements[5], elements[6], elements[7], isakmpData
        );
        isakmpData.setForeign(this.isakmpLineSupport.isForeign(isakmpData.getServerIp()));

        if (s2dFlag){
            if (isakmpData.getDataType() == -1){
                this.isakmpLineSupport.setMalformedPayload(elements[30],elements[29],  isakmpData);
            }

            this.isakmpLineSupport.set5TupleOuter(elements[22],elements[21],  elements[24], elements[23], elements[25], isakmpData);
            isakmpData.setUserId(elements[27])
                    .setServerId(elements[26]);
        }else{
            if (isakmpData.getDataType() == -1){
                this.isakmpLineSupport.setMalformedPayload(elements[29],elements[30],  isakmpData);
            }
            this.isakmpLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], isakmpData);
            isakmpData.setUserId(elements[26])
                    .setServerId(elements[27]);
        }



    }

    private void setFixProperties(String[] elements, IsakmpData isakmpData) {
        long capTimeN = Long.parseLong(elements[2]);
        this.isakmpLineSupport.setTargetName(elements[17], isakmpData);
        this.isakmpLineSupport.setGroupName(isakmpData);
        isakmpLineSupport.setTime(capTimeN,Long.parseLong(elements[3]),isakmpData);
        isakmpData.setSource(elements[16])
                .setImsi(SourceFieldUtils.parseStringStr(elements[18]))
                .setImei(SourceFieldUtils.parseStringStr(elements[19]))
                .setMsisdn(SourceFieldUtils.parseStringStr(elements[20]))
                .setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        isakmpData.setMacOuter(SourceFieldUtils.parseBooleanStr(elements[28]));

    }

    private void setVersion1(String[] elements, IsakmpExtension isakmpExtension) {
        boolean s2dFlag = true;
        String sdFlag = elements[29];
        if (Objects.equals(sdFlag,"D2S")){
            s2dFlag = false;
        }
        JSONObject jsonObject = new JSONObject();
        List<String> messageList = new ArrayList<>();
        Set<JSONObject> initiatorInformation = new LinkedHashSet<>();
        Set<JSONObject> responderInformation = new LinkedHashSet<>();
        Set<JSONObject> initiatorVid = new LinkedHashSet<>();
        Set<JSONObject> responderVid = new LinkedHashSet<>();
        for (int i = 34; i < elements.length; i++) {
             if (elements[i].isEmpty()) {
                continue;
            }
            if ("S2D".equals(elements[i]) || "D2S".equals(elements[i])) {
                s2dFlag = !elements[i].equals("D2S");
                continue;
            }
            String[] kv = elements[i].split(":");
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0].trim();
            String value = kv[1].trim();
            if (key.isEmpty() || value.isEmpty()) {
                continue;
            }
            if (key.equals("Exchange Type")) {
                if (s2dFlag) {
                    messageList.add("initiator:" + value);
                } else {
                    messageList.add("responder:" + value);
                }
                continue;
            }
            switch (key) {
                case "payload":
                    if (s2dFlag) {
                        messageList.add("initiator:" + value);
                        if (!jsonObject.isEmpty()) {
                            initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    } else {
                        messageList.add("responder:" + value);
                        if (!jsonObject.isEmpty()) {
                            responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    }
                    break;
                case "Transform":
                    if (jsonObject.isEmpty()) {
                        continue;
                    }
                    if (s2dFlag) {
                        initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    } else {
                        responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    }
                    break;
                case "Vendor Id":
                    JSONObject json = new JSONObject();
                    if (s2dFlag) {
                        json.put("Vendor ID", getVid(value));
                        initiatorVid.add(json);
                    } else {
                        json.put("Vendor ID", getVid(value));
                        responderVid.add(json);
                    }
                    break;
                case "Cert Encoding":
                    jsonObject.put("Cert Encoding",fixCertEncoding(value));
                    break;
                default:
                    break;
            }
            jsonObject.put(key, value);
        }
        isakmpExtension.setMessageList(messageList);
        isakmpExtension.setInitiatorInformation(initiatorInformation);
        isakmpExtension.setResponderInformation(responderInformation);
        isakmpExtension.setInitiatorVid(initiatorVid);
        isakmpExtension.setResponderVid(responderVid);
        isakmpExtension.setVersion(1);
    }

    /**
     * 将value转换成对应的中文
     */
    private String fixCertEncoding(String value) {

        switch (value){
            case "PKCS #7 wrapped X.509 certificate":
                value = "PKCS#7包装的X.509证书";
                break;
            case "PGP Certificate":
                value = "PGP证书";
                break;
            case "DNS Signed Key":
                value = "DNS签名密钥";
                break;
            case "X.509 Certificate - Signature":
                value = "X.509签名证书";
                break;
            case "X.509 Certificate - Key Exchange":
                value = "X.509加密证书";
                break;
            case "Kerberos Tokens":
                value = "Kerberos令牌";
                break;
            case "Certificate Revocation List (CRL)":
                value = "证书吊销列表（CRL）";
                break;
            case "Authority Revocation List (ARL)":
                value = "授权撤销列表（ARL）";
                break;
            case "SPKI Certificate":
                value = "SPKI证书";
                break;
            case "X.509 Certificate - Attribute":
                value = "X.509属性证书";
                break;
        }
        return value;
    }

    private void setVersion2(String[] elements, IsakmpExtension isakmpExtension) {
        String sdFlag = elements[29];
        boolean s2dFlag = true;
        if (Objects.equals(sdFlag,"D2S")){
            s2dFlag = false;
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject vidJsonObject = new JSONObject();
        List<String> messageList = new ArrayList<>();
        Set<JSONObject> initiatorInformation = new LinkedHashSet<>();
        Set<JSONObject> responderInformation = new LinkedHashSet<>();
        Set<JSONObject> initiatorVid = new LinkedHashSet<>();
        Set<JSONObject> responderVid = new LinkedHashSet<>();
        for (int i = 34; i < elements.length; i++) {
            if (elements[i].isEmpty()) {
                continue;
            }
            if ("S2D".equals(elements[i]) || "D2S".equals(elements[i])) {
                s2dFlag = !elements[i].equals("D2S");
                continue;
            }
            String[] kv = elements[i].split(":");
            if (kv.length != 2) {
                continue;
            }
            String key = formatKey(kv[0]);
            String value = kv[1].trim();
            if (key.isEmpty() || value.isEmpty()) {
                continue;
            }
            switch (key) {
                case "Exchange Type":
                    if (s2dFlag) {
                        messageList.add("initiator:" + value);
                    } else {
                        messageList.add("responder:" + value);
                    }
                    break;
                case "payload":
                    if (s2dFlag) {
                        messageList.add("initiator:" + value);
                        if (!jsonObject.isEmpty()) {
                            initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    } else {
                        messageList.add("responder:" + value);
                        if (!jsonObject.isEmpty()) {
                            responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    }
                    break;
                case "Transform":
                    if (jsonObject.isEmpty()) {
                        continue;
                    }
                    if (s2dFlag) {
                        initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    } else {
                        responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    }
                    break;
                case "Vendor ID":
                    if (s2dFlag) {
                        initiatorVid.add((JSONObject) ToolUtils.clone(vidJsonObject));
                    } else {
                        responderVid.add((JSONObject) ToolUtils.clone(vidJsonObject));
                    }
                    vidJsonObject = new JSONObject();
                    vidJsonObject.put(key, getVid(value));
                    break;
                case "CheckPoint":
                    vidJsonObject.put(key, value);
                    break;
                case "CheckPoint Product":
                    vidJsonObject.put(key, value);
                    break;
                case "CheckPoint Version":
                    vidJsonObject.put(key, value);
                    break;
                case "cert_encoding":
                    jsonObject.put("Cert Encoding",fixCertEncoding(value));
                    break;
                default:
                    break;
            }
            jsonObject.put(key, value);
        }
        isakmpExtension.setMessageList(messageList);
        isakmpExtension.setInitiatorInformation(initiatorInformation);
        isakmpExtension.setResponderInformation(responderInformation);
        isakmpExtension.setInitiatorVid(initiatorVid);
        isakmpExtension.setResponderVid(responderVid);
        isakmpExtension.setVersion(2);
    }

    private String getVid(String str) {
        if (str.contains("(") && str.endsWith(")")) {
            String[] elements = str.split("\\(");
            elements[1] = elements[1].substring(0, elements[1].length() - 1);
            if (elements[1].isEmpty()) {
                return elements[0];
            } else {
                return elements[1];
            }
        }
        return str;
    }

    private String formatKey(String key) {
        while (key.contains("(") && key.contains(")")) {
            String[] buffer = key.split("\\(", -1);
            key = buffer[0] + buffer[1].split("\\)", -1)[1];
        }
        key = key.trim();
        key = key.replaceAll(" ", "_").replaceAll("-", "_").toLowerCase();
        return key;
    }

}
