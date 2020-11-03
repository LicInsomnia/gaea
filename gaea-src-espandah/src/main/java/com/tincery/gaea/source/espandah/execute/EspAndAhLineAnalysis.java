package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Insomnia
 */
@Component
public class EspAndAhLineAnalysis implements SrcLineAnalysis<EspAndAhData> {

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
    public EspAndAhData pack(String line) {
        EspAndAhData espAndAhData = new EspAndAhData();
        String[] elements = StringUtils.FileLineSplit(line);
        return espAndAhData;
    }

}
