package com.tincery.gaea.source.pptpandl2tp.execute;


import com.tincery.gaea.api.src.Pptpandl2tpData;
import com.tincery.gaea.api.src.SshData;
import com.tincery.gaea.api.src.extension.SshExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author gxz
 */

@Component
public class Pptpandl2tpLineAnalysis implements SrcLineAnalysis<Pptpandl2tpData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 0.syn 1.fin 2.startTime 3.endTime 4.uppkt 5. upbyte 6. downpkt 7.downbyte
     * 8.datatype(0→PPTP 1->L2tp -1->malformed)
     * 9.protocol 10.serverMac 11.clientMac 12.serverIp_n
     * 13.clientIp_n 14.serverPort 15.clientPort 16.source
     * 17.runleName 18.imsi 28.imei 20.msisdn
     * 21.outclientip 22.outserverip 23.outclientport
     * 24.outserverport 25.outproto 26.userid 27.serverid 28.ismac2outer
     * ----------------------------以下是datatype！=-1的值------------------------------------
     * 29.response 30.challenge 31.responseName 32.challengeName
     * 33.authProtocol 34.authAlgo 35.successMesg 36.EncAlog
     * -----------------------------datatype = -1 ----------------------------------
     * 29.upPayload 30.downPayload
     */
    @Override
    public Pptpandl2tpData pack(String line) {
        Pptpandl2tpData pptpandl2tpData = new Pptpandl2tpData();
        String[] elements = StringUtils.FileLineSplit(line);

        fixCommon(elements,pptpandl2tpData);
        Integer dataType = pptpandl2tpData.getDataType();
        if (dataType!= -1){
            fixPptpAndL2tp(elements,pptpandl2tpData);
        }else{
            fixMalformed(elements,pptpandl2tpData);
        }

        return pptpandl2tpData;
    }

    /**
     * 填装malformed
     */
    public void fixMalformed(String[] elements,Pptpandl2tpData data){
        srcLineSupport.setMalformedPayload(elements[29], elements[30], data);
    }

    /**
     * 装填pptp 和 l2tp
     */
    public void fixPptpAndL2tp(String[] elements,Pptpandl2tpData data){
        data.setResponse(elements[29])
                .setChallenge(elements[30])
                .setResponseName(elements[31])
                .setChallengeName(elements[32])
                .setAuthProtocol(elements[33])
                .setAuthAlgo(elements[34])
                .setSuccessMesg(elements[35])
                .setEncAlog(elements[36]);
    }
    /**
     * 装填common
     * @param elements
     * @param pptp
     */
    public void fixCommon(String[] elements,Pptpandl2tpData pptp){
        pptp.setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        this.srcLineSupport.setTime(Long.parseLong(elements[2]),Long.parseLong(elements[3]),pptp);
        this.srcLineSupport.setFlow(elements[4], elements[5], elements[6], elements[7], pptp);
        pptp.setDataType(Integer.parseInt(elements[8]));
        this.srcLineSupport.set7Tuple(elements[10],
                elements[11],
                elements[12],
                elements[13],
                elements[14],
                elements[15],
                elements[9],
                // proName 赋默认值  如果匹配到了相关application 会替换掉proName
                HeadConst.PRONAME.PPTPANDL2TP,
                pptp
        );
        pptp.setSource(elements[16]);
        this.srcLineSupport.setTargetName(elements[17], pptp);
        this.srcLineSupport.setGroupName(pptp);
        pptp.setImsi(elements[18])
                .setImei(elements[19])
                .setMsisdn(elements[20]);
        srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], pptp);
        pptp.setUserId(elements[26])
                .setServerId(elements[27]);
        pptp.setMacOuter("1".equals(elements[28]));
    }



}
