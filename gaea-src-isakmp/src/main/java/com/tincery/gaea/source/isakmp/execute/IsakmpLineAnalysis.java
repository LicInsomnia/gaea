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
     * 如果is_first是0  进行其他判断。并按照malformed处理,装载基础属性，直接返回
     *
     * 装载extension：
     * 先拿到29位置（固定位置） 的S2D 或D2S 来决定s2dFlag 进行之后的装填
     * 1.记录29位置的值
     * 2.当碰到D2S 或S2D的时候进行判断  如果和记录的值一致，为发起方init  不是为响应方responder
     * 根据循环拿到的字段装填属性
     * 特殊处理：如果遇到了S2D或D2S 不仅要进行s2dFlag的变换 还要进行一次payload(装载数据进入集合)，以免丢失数据或数据装载错误
     * 数据装箱之后，进行adjust。
     * 如果是malformed  装填非标准IPSEC  如果datatype是1 装填其他属性
     *
     **/
    @Override
    public IsakmpData pack(String line) throws Exception {
        IsakmpData isakmpData = new IsakmpData();
        String[] elements = StringUtils.FileLineSplit(line);
        isakmpData.setDataType(Integer.parseInt(elements[8]));
        setFixProperties(elements, isakmpData);

        Boolean s2dFlag = getS2DFlagByParam(elements[29]);

        if (!elements[30].startsWith("Is_first") && isakmpData.getDataType() != -1) {
            throw new Exception("Is_first字段标记错误");
        }
        if (isakmpData.getDataType() == -1){
            fixCommonMalformed(isakmpData,s2dFlag,elements);
            return isakmpData;
        }
        if (elements[30].endsWith("0")) {
            /*使用第二种判断方式 装填common后直接返回*/
            fixCommonMalformed(isakmpData,s2dFlag,elements);
            isakmpData.setDataType(-2);
            return isakmpData;
        }
        fixCommonNormal(isakmpData,elements,s2dFlag);
        return isakmpData;
    }

    /**
     * 根据传递进来的参数 给出s2dFlag
     * @param element 数据element[29]
     * @return boolean
     */
    private Boolean getS2DFlagByParam(String element) {
        if ("D2S".equals(element)) {
            return false;
        }else if (Objects.equals("S2D",element)){
            return true;
        }
        return null;
    }

    /**
     * 装填正常的数据
     * @param isakmpData 实体
     * @param elements 源
     * @param s2dFlag 判断依据
     */
    private void fixCommonNormal(IsakmpData isakmpData, String[] elements, Boolean s2dFlag) throws IllegalArgumentException {
        /*根据s2dFlag 判断用什么装填方式*/
        fixCommonByS2DFlag(isakmpData,elements,s2dFlag);
        IsakmpExtension isakmpExtension = new IsakmpExtension();
        int version = Integer.parseInt(elements[33].split(":")[1].trim());
        switch (version) {
            case 1:
                fixVersion(elements,isakmpExtension,1);
                break;
            case 2:
                fixVersion(elements,isakmpExtension,2);
                break;
            default:
                throw new IllegalArgumentException("Version版本标记错误");
        }
        isakmpExtension.setExtension();
        isakmpData.setIsakmpExtension(isakmpExtension);
    }

    /**
     * 装填malformed属性
     * @param isakmpData 实体
     * @param s2dFlag 装填顺序
     * @param elements 源
     */
    private void fixCommonMalformed(IsakmpData isakmpData, Boolean s2dFlag, String[] elements) {
        s2dFlag = getS2DFlag(s2dFlag,elements);
        fixCommonByS2DFlag(isakmpData,elements,s2dFlag);
        isakmpData.setIsakmpExtension(new IsakmpExtension());
    }

    private void fixVersion(String[] elements,IsakmpExtension isakmpExtension,Integer version){
        /*创建各种变量*/
        String sdFlag = elements[29];
        boolean s2dFlag = true;
        JSONObject jsonObject = new JSONObject();
        List<String> messageList = new ArrayList<>();
        Set<JSONObject> initiatorInformation = new LinkedHashSet<>();
        Set<JSONObject> responderInformation = new LinkedHashSet<>();
        Set<JSONObject> initiatorVid = new LinkedHashSet<>();
        Set<JSONObject> responderVid = new LinkedHashSet<>();

        isakmpExtension.setInitiatorSPI(elements[31].split(":")[1]);
        isakmpExtension.setResponderSPI(elements[32].split(":")[1]);
        //version2 独有
        JSONObject vidJsonObject = new JSONObject();
        /*取值*/
        for (int i = 34; i < elements.length; i++) {
            /* version1 和 version2的共同之处  在于判空 */
            if (elements[i].isEmpty()) {
                continue;
            }
            if ("S2D".equals(elements[i]) || "D2S".equals(elements[i])) {
                if (s2dFlag) {
                    if (!jsonObject.isEmpty()) {
                        initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    }
                } else {
                    if (!jsonObject.isEmpty()) {
                        responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                        jsonObject = new JSONObject();
                    }
                }
                s2dFlag = elements[i].equals(sdFlag);
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

            /*version 1 独有的装载*/
            if (key.equals("Exchange Type") && Objects.equals(1,version)) {
                if (s2dFlag) {
                    messageList.add("initiator:" + value);
                } else {
                    messageList.add("responder:" + value);
                }
                jsonObject.put("Exchange Type",value);
                continue;
            }

            switch (key) {
                /* version2独有的 */
                case "Exchange Type":
                    if (Objects.equals(2,version)){
                        if (s2dFlag) {
                            messageList.add("initiator:" + value);
                        } else {
                            messageList.add("responder:" + value);
                        }
                        jsonObject.put("Exchange Type",value);
                        break;
                    }
                case "payload":
                    boolean flag = false;
                    if (s2dFlag) {
                        messageList.add("initiator:" + value);
                        if (!jsonObject.isEmpty()) {
                            flag = addHash(value, jsonObject);
                            initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    } else {
                        messageList.add("responder:" + value);
                        if (!jsonObject.isEmpty()) {
                            flag = addHash(value,jsonObject);
                            responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                            jsonObject = new JSONObject();
                        }
                    }
                    if (flag){
                        continue;
                    }
                    break;
                case "Transform":
                    if (jsonObject.isEmpty()) {
                        continue;
                    }
                    if (s2dFlag) {
                        initiatorInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                    } else {
                        responderInformation.add((JSONObject) ToolUtils.clone(jsonObject));
                    }
                    jsonObject = new JSONObject();
                    break;
                case "Vendor Id":
                    JSONObject json = new JSONObject();
                    if (s2dFlag) {
                        if (Objects.equals(1,version)){
                            json.put("Vendor ID", getVid(value));
                            initiatorVid.add(json);
                        }else if (Objects.equals(2,version)){
                            initiatorVid.add((JSONObject) ToolUtils.clone(vidJsonObject));
                        }
                    } else {
                        if (Objects.equals(1,version)){
                            json.put("Vendor ID", getVid(value));
                            responderVid.add(json);
                        }else if (Objects.equals(2,version)){
                            responderVid.add((JSONObject) ToolUtils.clone(vidJsonObject));
                        }
                    }
                    if (Objects.equals(2,version)){
                        vidJsonObject = new JSONObject();
                        vidJsonObject.put(key, getVid(value));
                    }
                    break;
                case "CheckPoint":
                    if (Objects.equals(2,version)){
                        vidJsonObject.put(key, value);
                        break;
                    }

                case "CheckPoint Product":
                    if (Objects.equals(2,version)){
                        vidJsonObject.put(key, value);
                        break;
                    }

                case "CheckPoint Version":
                    if (Objects.equals(2,version)){
                        vidJsonObject.put(key, value);
                        break;
                    }
                default:
                    break;
            }
            jsonObject.put(key, value);
        }

        /*装载值*/
        isakmpExtension.setMessageList(messageList);
        isakmpExtension.setInitiatorInformation(initiatorInformation);
        isakmpExtension.setResponderInformation(responderInformation);
        isakmpExtension.setInitiatorVid(initiatorVid);
        isakmpExtension.setResponderVid(responderVid);
        isakmpExtension.setVersion(version);
    }

    private boolean addHash(String value,JSONObject jsonObject){
        if (value.contains("Hash")){
            jsonObject.put("payload",value);
            return true;
        }
        return false;
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
            assert this.isakmpLineSupport != null;
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

    private String fixCert(String value) {
        if (StringUtils.isEmpty(value)){
            return null;
        }
        String[] split = value.split("_");
        if (split.length<2){
            return null;
        }
        return split[0];
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

}
