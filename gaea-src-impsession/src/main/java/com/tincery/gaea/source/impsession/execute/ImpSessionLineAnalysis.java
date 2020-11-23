package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gongxuanzhang
 */
@Component
@Slf4j
public class ImpSessionLineAnalysis implements SrcLineAnalysis<ImpSessionData> {

    @Autowired
    private SrcLineSupport srcLineSupport;

    /***
     *
     * 0.syn/synack       1.fin               2.startTime         3.endTime
     * 4.uppkt            5.upbyte            6.downpkt           7.downbyte
     * 8.protocol         9.smac              10.dMac             11.sip_n
     * 12.dip_n           13.sport            14.dport            15.protocol
     * 16.source          17.ruleName         18.imsi             19.imei
     * 20.msisdn          21.outclientip      22.outserverip      23.outclientport
     * 24.outserverport   25.outproto         26.userid           27.serverid
     * 28.ismac2outer      29.payload
     **/
    @Override
    public ImpSessionData pack(String line) {
        ImpSessionData impSessionData = new ImpSessionData();
        String[] elements = StringUtils.FileLineSplit(line);
        long capTime = Long.parseLong(elements[2]);
        long endTime = Long.parseLong(elements[3]);
        srcLineSupport.setTime(capTime,endTime,impSessionData);
        impSessionData.setSource(elements[16])
                .setSyn("1".equals(elements[0]))
                .setFin("1".equals(elements[1]))
                .setDataType(Integer.parseInt(elements[8]));
        this.srcLineSupport.setMobileElements(elements[18], elements[19], elements[20], impSessionData);
        this.srcLineSupport.setPartiesId(elements[26], elements[27], impSessionData);
        this.srcLineSupport.setTargetName(elements[17], impSessionData);
        this.srcLineSupport.setGroupName(impSessionData);
        this.srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], impSessionData);
        impSessionData.setPayload(elements[29]);
        impSessionData.setMacOuter("1".equals(elements[28]));
        setImpSessionDataFix(impSessionData, elements);
        if (needCorrect(elements)) {
            modifyImpSessionData(impSessionData, elements);
        }
        try {
            impSessionData.setForeign(this.srcLineSupport.isForeign(impSessionData.getServerIp()));
        }catch (RuntimeException e){
            impSessionData.setForeign(false);
            log.error("无法判断ipv6内外网，默认设置为false，数据为{}",line);
        }

        return impSessionData;
    }

    /****
     * 是否需要IP矫正
     **/
    private boolean needCorrect(String[] element) {
        return (Integer.parseInt(element[15]) == 17) &&
                (element[11].length() <= 10) &&
                this.srcLineSupport.isInnerIp(element[11]) &&
                !this.srcLineSupport.isInnerIp(element[12]);
    }

    private void setImpSessionDataFix(ImpSessionData impSessionData, String[] element) {
        this.srcLineSupport.set7Tuple(
                element[9],
                element[10],
                element[11],
                element[12],
                element[13],
                element[14],
                element[15],
                "other",
                impSessionData
        );
        this.srcLineSupport.setFlow(
                element[4],
                element[5],
                element[6],
                element[7],
                impSessionData
        );
    }

    private void modifyImpSessionData(ImpSessionData impSessionData, String[] element) {
        impSessionData.setServerMac(element[10])
                .setClientMac(element[9])
                .setServerIp(NetworkUtil.arrangeIp(element[12]))
                .setClientIp(NetworkUtil.arrangeIp(element[11]))
                .setServerPort(Integer.parseInt(element[14]))
                .setClientPort(Integer.parseInt(element[13]))
                .setUpPkt(Long.parseLong(element[6]))
                .setUpByte(Long.parseLong(element[7]))
                .setDownPkt(Long.parseLong(element[4]))
                .setDownByte(Long.parseLong(element[5]));

    }


}
