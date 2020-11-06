package com.tincery.gaea.source.isakmp.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.IsakmpData;
import com.tincery.gaea.api.src.extension.IsakmpExtension;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
     **/
    @Override
    public IsakmpData pack(String line) throws Exception {
        IsakmpData isakmpData = new IsakmpData();
        String[] elements = StringUtils.FileLineSplit(line);
        boolean s2dFlag = true;
        isakmpData.setDataType(Integer.parseInt(elements[8]));
        setFixProperties(elements, isakmpData);
        if (isakmpData.getDataType() == -1) {
            this.isakmpLineSupport.set7TupleAndFlow(s2dFlag, elements[10], elements[11], elements[12], elements[13],
                    elements[14], elements[15], elements[4], elements[5], elements[6], elements[7], isakmpData
            );
            isakmpData.setForeign(this.isakmpLineSupport.isForeign(isakmpData.getServerIp()));
            this.isakmpLineSupport.setMalformedPayload(elements[29], elements[30], isakmpData);
            return isakmpData;
        }
        if (!elements[30].startsWith("Is_first")) {
            throw new Exception("Is_first字段标记错误");
        }
        if (elements[30].endsWith("0")) {
            return null;
        }
        if ("D2S".equals(elements[29])) {
            s2dFlag = false;
        }
        this.isakmpLineSupport.set7TupleAndFlow(s2dFlag, elements[10], elements[11], elements[12], elements[13],
                elements[14], elements[15], elements[4], elements[5], elements[6], elements[7], isakmpData
        );
        isakmpData.setForeign(this.isakmpLineSupport.isForeign(isakmpData.getServerIp()));
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

    private void setFixProperties(String[] elements, IsakmpData isakmpData) {
        long capTimeN = Long.parseLong(elements[2]);
        this.isakmpLineSupport.setTargetName(elements[17], isakmpData);
        this.isakmpLineSupport.setGroupName(isakmpData);
        isakmpLineSupport.setTime(capTimeN,Long.parseLong(elements[3]),isakmpData);
        isakmpData.setSource(elements[16])
                .setImsi(SourceFieldUtils.parseStringStr(elements[18]))
                .setImei(SourceFieldUtils.parseStringStr(elements[19]))
                .setMsisdn(SourceFieldUtils.parseStringStr(elements[20]))
                .setUserId(elements[26])
                .setServerId(elements[27])
                .setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        isakmpData.setMacOuter(SourceFieldUtils.parseBooleanStr(elements[28]));
        this.isakmpLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], isakmpData);
    }

    private void setVersion1(String[] elements, IsakmpExtension isakmpExtension) {
        String sdFlag = elements[29];
        boolean s2dFlag = true;
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
            if (("S2D".equals(elements[i]) || "D2S".equals(elements[i])) && !elements[i].equals(sdFlag)) {
                s2dFlag = false;
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
                case "Vendor ID":
                    JSONObject json = new JSONObject();
                    if (s2dFlag) {
                        json.put("Vendor ID", getVid(value));
                        initiatorVid.add(json);
                    } else {
                        json.put("Vendor ID", getVid(value));
                        responderVid.add(json);
                    }
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
    }

    private void setVersion2(String[] elements, IsakmpExtension isakmpExtension) {
        String sdFlg = elements[29];
        boolean s2dFlg = true;
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
            if (("S2D".equals(elements[i]) || "D2S".equals(elements[i])) && !elements[i].equals(sdFlg)) {
                s2dFlg = false;
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
                    if (s2dFlg) {
                        messageList.add("initiator:" + value);
                    } else {
                        messageList.add("responder:" + value);
                    }
                    break;
                case "payload":
                    if (s2dFlg) {
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
                    if (s2dFlg) {
                        initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    } else {
                        responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    }
                    break;
                case "Vendor ID":
                    if (s2dFlg) {
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
