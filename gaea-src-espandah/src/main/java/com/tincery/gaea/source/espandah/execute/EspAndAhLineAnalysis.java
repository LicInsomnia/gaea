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
        this.espAndAhLineSupport.setCommon(elements[2], elements[3], elements[4], elements[5],
                elements[6], elements[7], elements[1], "", elements[21], elements[22], elements[25], data);
        data.setCapTime(DateUtils.validateTime(Long.parseLong(elements[0])))
                .setSource(elements[8]);
        data.setProtocol(elements[1]);
        if (!data.set(elements[2], elements[3], elements[4], elements[5], elements[6], elements[7], elements[21], elements[22], elements[25])) {
            return null;
        }
        data.setImsi(elements[10]);
        data.setImei(elements[11]);
        data.setMsisdn(elements[12]);
        data.setUserId(elements[18]);
        data.setServerId(elements[19]);
        if (!data.setSpi(elements[23])) {
            return null;
        }
        data.setTargetName(elements[9], userId2TargetName, target2Group);
        data.setOuterFromMac(elements[20]);
        data.checkIsForeign(ipCheckUtils);
    }

}
