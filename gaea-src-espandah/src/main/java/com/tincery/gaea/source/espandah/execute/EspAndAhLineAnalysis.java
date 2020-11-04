package com.tincery.gaea.source.espandah.execute;

import ch.qos.logback.core.util.TimeUtil;
import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
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
     * 0.timestamp 1.prococol 2.smac 3.dmac 4.sip_n 5.dip_n
     * 6.sport 7.dport 8.source 9.rulename 10.imsi
     *  11.imei 12.msisdn 13.outsip_n 14.outdip_n 15.outsport
     *  16.outdport 17.outproto 18.userid 19.serverid
     *  20.ismac2outer 21.pkt
     *  22.byte 23.dataType(0/1)
     *  24.seq_num
     *  25. payload（最多48字节）
     **/
    @Override
    public EspAndAhData pack(String line) {
        EspAndAhData espAndAhData = new EspAndAhData();
        String[] elements = StringUtils.FileLineSplit(line);

        fixCommon(elements,espAndAhData);

        return espAndAhData;
    }

    private void fixCommon(String[] elements,EspAndAhData espAndAhData){
        espAndAhData.setCapTime(DateUtils.validateTime(Long.parseLong(elements[0])));
    }

}
