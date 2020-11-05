package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Insomnia
 */
@Component
public class EspAndAhLineAnalysis implements SrcLineAnalysis<EspAndAhData> {

    @Autowired
    private EspAndAhLineSupport espAndAhLineSupport;

    /**
     * 0.timestamp      1.prococol      2.smac      3.dmac      4.sip_n
     * 5.dip_n          6.sport         7.dport     8.source	9.rulename
     * 10.imsi          11.imei         12.msisdn   13.outsip_n 14.outdip_n
     * 15.outsport      16.outdport     17.outproto             18.userid
     * 19.serverid      20.ismac2outer  21.pkt      22.byte     23.spi
     * 24.seq_num       25.payload
     */
    @Override
    public EspAndAhData pack(String line) {
        EspAndAhData espAndAhData = new EspAndAhData();
        String[] elements = StringUtils.FileLineSplit(line);
        fixCommon(elements, espAndAhData);
        return espAndAhData;
    }

    private void fixCommon(String[] elements, EspAndAhData data) {
        this.espAndAhLineSupport.setCommon(elements, data);
        data.setCapTime(DateUtils.validateTime(Long.parseLong(elements[0])))
                .setSource(elements[8])
                .setImsi(elements[10])
                .setImei(elements[11])
                .setMsisdn(elements[12])
                .setUserId(elements[18])
                .setServerId(elements[19])
                .setFin(false)
                .setSyn(true);
        data.setMacOuter("1".equals(elements[20]));
        data.setEndTime(data.getCapTime());
        this.espAndAhLineSupport.setTargetName(elements[9], data);
        this.espAndAhLineSupport.setGroupName(data);
        data.setForeign(this.espAndAhLineSupport.isForeign(data.getServerIp()));
    }

}
