package com.tincery.gaea.source.pptpandl2tp.execute;


import com.tincery.gaea.api.src.Pptpandl2tpData;
import com.tincery.gaea.api.src.extension.PptpAndL2tpExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * @author gxz
 */

@Component
@Slf4j
public class Pptpandl2tpLineAnalysis implements SrcLineAnalysis<Pptpandl2tpData> {


    @Autowired
    public PptpAndL2TPLineSupport pptpAndL2TPLineSupport;

    /**
     * 0.syn 1.fin 2.startTime 3.endTime 4.uppkt 5. upbyte 6. downpkt 7.downbyte
     * 8.datatype(0→PPTP 1->L2tp -1->malformed)
     * 9.protocol 10.serverMac 11.clientMac 12.serverIp_n
     * 13.clientIp_n 14.serverPort 15.clientPort 16.source
     * 17.runleName 18.imsi 28.imei 20.msisdn
     * 21.outclientip 22.outserverip 23.outclientport
     * 24.outserverport 25.outproto 26.userid 27.serverid 28.ismac2outer
     * ----------------------------以下是datatype！=-1的值------------------------------------
     * 29.challenge 30.response   31.challengeName 32.responseName
     * 33.authProtocol 34.authAlgo 35.successMesg 36.EncAlog
     * -----------------------------datatype = -1 ----------------------------------
     * 29.upPayload 30.downPayload
     */

    @Override
    public Pptpandl2tpData pack(String line) throws Exception {
        Pptpandl2tpData pptpandl2tpData = new Pptpandl2tpData();
        String[] elements = StringUtils.FileLineSplit(line);
        /*
         * 新填入了填装逻辑 具体见220 http://172.16.1.220:9080/display/Document/gaea-src-pptpandl2tp
         * 1.根据 challenge/challengeName.startWith 和 response/responseName.startWith 决定一个布尔值
         * 2.如果布尔值为空 则端口判别
         * 3.如果布尔值为空 则srcip是内网地址，dstip是外网地址
         * 4.如果布尔值为空 则端口大小判别
         * common属性 均由该布尔值提供
         */
        int dataType = Integer.parseInt(elements[8]);
        Boolean isD2SServer = getIsD2SServer(dataType, elements, pptpandl2tpData);
        PptpAndL2tpExtension pptpAndL2tpExtension = new PptpAndL2tpExtension();
        fixCommon(elements, pptpandl2tpData,isD2SServer);
        if (dataType != -1) {
            fixPptpAndL2tp(elements, pptpAndL2tpExtension);
            if (dataType == 0){
                pptpandl2tpData.setProName(HeadConst.PRONAME.PPTP);
            }else {
                pptpandl2tpData.setProName(HeadConst.PRONAME.L2TP);
            }
        } else {
            fixMalformed(elements, pptpandl2tpData);
            pptpandl2tpData.setProName(HeadConst.PRONAME.OTHER);
        }
        //设置foreign
        try {
            pptpandl2tpData.setForeign(pptpAndL2TPLineSupport.isForeign(pptpandl2tpData.getServerIp()));
        }catch (RuntimeException e){
            pptpandl2tpData.setForeign(false);
            log.error("无法判断ipv6内外网，默认设置为false，数据为{}",line);
        }

        pptpandl2tpData.setPptpAndL2tpExtension(pptpAndL2tpExtension);
        return pptpandl2tpData;
    }

    private Boolean getIsD2SServer(Integer dataType,String[] elements,Pptpandl2tpData pptpandl2tpData) throws Exception {
        Boolean isD2SServer = null;
        if (dataType != -1){
            isD2SServer = sureIsD2SServer(elements, pptpandl2tpData);
            if (Objects.isNull(isD2SServer)){
                /* 如果这里没有取到isD2SServer 说明后面可能没有数据 或者没有challenge、response 用其他办法 */
                isD2SServer = pptpAndL2TPLineSupport.sureisD2SServerByPortAndDataType(dataType, Integer.parseInt(elements[14]), Integer.parseInt(elements[15]), isD2SServer);
            }
        }
        if (Objects.isNull(isD2SServer)){
            try {
                isD2SServer = pptpAndL2TPLineSupport.sureisD2SServerByIsInnerIp(elements[12],elements[13],isD2SServer);
            }catch (Exception e){
                isD2SServer = pptpAndL2TPLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]),Integer.parseInt(elements[15]));
            }
            if (Objects.isNull(isD2SServer)){
                isD2SServer = pptpAndL2TPLineSupport.sureisD2SServerByComparePort(Integer.parseInt(elements[14]),Integer.parseInt(elements[15]));
            }
        }
        return isD2SServer;
    }


    /**
     * 返回d2sServer
     */
    private Boolean sureIsD2SServer(String[] elements, Pptpandl2tpData pptpandl2tpData) throws Exception {
        int dataType = Integer.parseInt(elements[8]);
        this.pptpAndL2TPLineSupport.fixForJudgeIsServer(pptpandl2tpData,dataType,elements[14],elements[15]);
        if (dataType != -1){
            for (int i = 29; i < elements.length; i++) {
                if (StringUtils.isEmpty(elements[i])) {
                    continue;
                }
                //第一个判断依据 数据中是否有符合标准的数据
                Boolean isServer = sureIsD2SServerByElements(elements[i],i);
                if (Objects.nonNull(isServer)){
                    return isServer;
                }
            }
        }
        return null;
    }

    private Boolean sureIsD2SServerByElements(String element,int index) throws Exception {
        if (StringUtils.isEmpty(element)){
            return  null;
        }
        String[] split = element.split(" ");
        if (split.length<2){
            throw new Exception("PPTP的extension数据格式错误");
        }
        Boolean isD2SServer = null;
        if (index <= 32){
            if (index % 2 == 0){
                if (Objects.equals(split[0],"D2S")){
                    isD2SServer = true;
                }else if (Objects.equals(split[0],"S2D")){
                    isD2SServer = false;
                }
            }else{
                if (Objects.equals(split[0],"D2S")){
                    isD2SServer = false;
                }else if (Objects.equals(split[0],"S2D")){
                    isD2SServer = true;
                }
            }
        }
        return isD2SServer;
    }

    /**
     * 填装malformed
     */
    public void fixMalformed(String[] elements, Pptpandl2tpData data) {
        pptpAndL2TPLineSupport.setMalformedPayload(elements[29], elements[30], data);
    }

    /**
     * 装填pptp 和 l2tp
     */
    public void fixPptpAndL2tp(String[] elements, PptpAndL2tpExtension pptpAndL2tpExtension) {
        pptpAndL2tpExtension.setResponse(subExtension(elements[30]))
                .setChallenge(subExtension(elements[29]))
                .setResponseName(subExtension(elements[32]))
                .setChallengeName(subExtension(elements[31]))
                .setAuthenticationProtocol(subExtension(elements[33]))
                .setAuthenticationAlgorithm(subExtension(elements[34]))
                .setSuccessMessage(subExtension(elements[35]))
                .setEncryptionAlgorithm(subExtension(elements[36]));
    }

    private String subExtension(String extension){
        if (StringUtils.isNotEmpty(extension)){
            if (extension.startsWith("S2D ") || extension.startsWith("D2S ")){
                return extension.substring(4);
            }
        }
        return null;
    }



    /**
     * 装填common
     */
    public void fixCommon(String[] elements, Pptpandl2tpData pptpandl2tpData,boolean isD2SServer) {
        fixByIsD2SServer(elements,pptpandl2tpData,isD2SServer);
        pptpandl2tpData.setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        this.pptpAndL2TPLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), pptpandl2tpData);
        pptpandl2tpData.setImsi(elements[18])
                .setImei(elements[19])
                .setMsisdn(elements[20]);
        pptpandl2tpData.setMacOuter("1".equals(elements[28]));
        pptpandl2tpData.setSource(elements[16]);
        pptpandl2tpData.setDataType(Integer.parseInt(elements[8]));
    }


    /**
     * isD2SServer  (D是否是Server)
     * true  dst为server  src 为client
     * false dst为client  src为server
     * @param elements
     * @param pptpandl2tpData
     * @param isD2SServer
     */
    private void fixByIsD2SServer(String[] elements, Pptpandl2tpData pptpandl2tpData, boolean isD2SServer) {

        if (isD2SServer){
            this.pptpAndL2TPLineSupport.setFlow(elements[4], elements[5], elements[6], elements[7], pptpandl2tpData);

            this.pptpAndL2TPLineSupport.set7Tuple(elements[10],
                    elements[11],
                    elements[12],
                    elements[13],
                    elements[14],
                    elements[15],
                    elements[9],
                    pptpandl2tpData
            );
            this.pptpAndL2TPLineSupport.setTargetName(elements[17], pptpandl2tpData);
            this.pptpAndL2TPLineSupport.setGroupName(pptpandl2tpData);
            pptpAndL2TPLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], pptpandl2tpData);
            pptpandl2tpData.setUserId(elements[26])
                    .setServerId(elements[27]);
        }else{
            this.pptpAndL2TPLineSupport.setFlow(elements[6], elements[7],elements[4], elements[5],  pptpandl2tpData);
            this.pptpAndL2TPLineSupport.set7Tuple(
                    elements[11],
                    elements[10],
                    elements[13],
                    elements[12],
                    elements[15],
                    elements[14],
                    elements[9],
                    pptpandl2tpData
            );
            this.pptpAndL2TPLineSupport.setTargetName(elements[17], pptpandl2tpData);
            this.pptpAndL2TPLineSupport.setGroupName(pptpandl2tpData);
            pptpAndL2TPLineSupport.set5TupleOuter( elements[22], elements[21], elements[24], elements[23],elements[25], pptpandl2tpData);
            pptpandl2tpData.setUserId(elements[27])
                    .setServerId(elements[26]);
        }

    }


}
