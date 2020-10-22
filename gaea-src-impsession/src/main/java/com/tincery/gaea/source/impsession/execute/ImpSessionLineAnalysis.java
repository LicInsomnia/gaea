package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gongxuanzhang
 */
@Component
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
        String[] element = StringUtils.FileLineSplit(line);
        long capTime = DateUtils.validateTime(Long.parseLong(element[2]));
        long endTime = DateUtils.validateTime(Long.parseLong(element[3]));
        impSessionData.setSyn("1".equals(element[0]))
                .setFin("1".equals(element[1]))
                .setDataType(Integer.parseInt(element[8]))
                .setCapTime(capTime)
                .setDurationTime(endTime - capTime)
                .setImsi(element[18])
                .setImei(element[19])
                .setMsisdn(element[20])
                .setUserId(element[26])
                .setServerId(element[27]);
        this.srcLineSupport.setTargetName(element[17], impSessionData);
        this.srcLineSupport.setGroupName(impSessionData);
        this.srcLineSupport.set5TupleOuter(element[21], element[22], element[23], element[24], element[25], impSessionData);
        impSessionData.setPayload(element[29]);
        impSessionData.setMacOuter("1".equals(element[28]));
        setImpSessionDataFix(impSessionData, element);
        if (needCorrect(element)) {
            modifyImpSessionData(impSessionData, element);
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
