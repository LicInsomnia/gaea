package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongxuanzhang
 */
@Component
public class ImpSessionLineAnalysis implements SrcLineAnalysis<ImpSessionData> {

    @Autowired
    private IpChecker ipChecker;

    @Autowired
    private GroupGetter groupGetter;

    private final Map<String, ImpSessionData> impSessionMap = new HashMap<>();

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
        setImpSessionDataFix(impSessionData, element);
        if (needCorrect(element)) {
            modifyImpSessionData(impSessionData, element);
        }
        return fill(impSessionData, element);
    }

    private ImpSessionData fill(ImpSessionData impSessionData, String[] element) {
        long captime = DateUtils.validateTime(Long.parseLong(element[2]));
        impSessionData.setCapTime(captime);
        long endtime = DateUtils.validateTime(Long.parseLong(element[3]));
        impSessionData.setDurationTime(endtime - captime);
        long duration = endtime - captime;
        impSessionData.setDurationTime(duration);
        impSessionData.setTargetName(element[17]);
        impSessionData.setGroupName(this.groupGetter.getGroupName(impSessionData.getTargetName()));
        impSessionData.setImsi(element[18]);
        impSessionData.setImei(element[19]);
        impSessionData.setMsisdn(element[20]);
        impSessionData.setPayload(element[29]);
        impSessionData.set5TupleOuter(element[21], element[22], element[23], element[24], element[25]);
        impSessionData.setUserId(element[26]);
        impSessionData.setServerId(element[27]);
        impSessionData.setMacOuter("1".equals(element[28]));
        String key = impSessionData.getKey();
        String pairKey = impSessionData.getPairKey();
        if (this.impSessionMap.containsKey(pairKey)) {
            ImpSessionData buffer = this.impSessionMap.get(pairKey);
            buffer.merge(impSessionData);
            this.impSessionMap.replace(pairKey, buffer);
        } else {
            this.impSessionMap.put(key, impSessionData);
        }
        return impSessionData;
    }

    /****
     * 是否需要IP矫正
     **/
    private boolean needCorrect(String[] element) {
        return (Integer.parseInt(element[15]) == 17) &&
                (element[11].length() <= 10) &&
                ipChecker.isInner(Long.parseLong(element[11])) && !ipChecker.isInner(Long.parseLong(element[12]));
    }

    private void setImpSessionDataFix(ImpSessionData impSessionData, String[] element) {
        impSessionData.setSyn("1".equals(element[0]));
        impSessionData.setFin("1".equals(element[1]));
        impSessionData.setDataType(Integer.parseInt(element[8]));


        impSessionData.setProtocol(Integer.parseInt(element[15]))
                .setServerMac(element[9])
                .setClientMac(element[10])
                .setServerIp(NetworkUtil.arrangeIp(element[11]))
                .setClientIp(NetworkUtil.arrangeIp(element[12]))
                .setServerPort(Integer.parseInt(element[13]))
                .setClientPort(Integer.parseInt(element[14]))
                .setProName("other")
                .setUpPkt(Long.parseLong(element[4]))
                .setUpByte(Long.parseLong(element[5]))
                .setDownPkt(Long.parseLong(element[6]))
                .setDownByte(Long.parseLong(element[7]));
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
