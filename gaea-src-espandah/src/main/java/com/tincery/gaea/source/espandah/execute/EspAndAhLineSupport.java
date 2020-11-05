package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.api.src.extension.EspAndAhExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.stereotype.Component;

@Component
public class EspAndAhLineSupport extends SrcLineSupport {

    public void setCommon(String[] elements,
                          EspAndAhData data
    ) throws NumberFormatException {
        String sMac = elements[2];
        String dMac = elements[3];
        String sIp = elements[4];
        String dIp = elements[5];
        String sPort = elements[6];
        String dPort = elements[7];
        String protocol = elements[1];
        String pkt = elements[21];
        String byteNum = elements[22];
        String payload = elements[25];
        EspAndAhExtension espAndAhExtension = new EspAndAhExtension();
        if (sIp.length() == 32 || dIp.length() == 32) {
            super.set7Tuple(sMac, dMac, sIp, dIp, sPort, dPort, protocol, HeadConst.PRONAME.OTHER, data);
            super.setFlow(pkt, "0", byteNum, "0", data);
            espAndAhExtension.setUpPayload(payload);
            data.setEspAndAhExtension(espAndAhExtension);
            return;
        }
        long sIpN = Long.parseLong(sIp);
        long dIpN = Long.parseLong(dIp);
        if (sIpN >= dIpN) {
            super.set7Tuple(sMac, dMac, sIp, dIp, sPort, dPort, protocol, HeadConst.PRONAME.OTHER, data);
            super.setFlow("0", pkt, "0", byteNum, data);
            espAndAhExtension.setUpPayload(payload);
            espAndAhExtension.setC2sSpi("");
            espAndAhExtension.setS2cSpi(elements[23]);
            data.setEspAndAhExtension(espAndAhExtension);
        } else {
            super.set7Tuple(dMac, sMac, dIp, sIp, dPort, sPort, protocol, HeadConst.PRONAME.OTHER, data);
            super.setFlow(pkt, "0", byteNum, "0", data);
            espAndAhExtension.setDownPayload(payload);
            espAndAhExtension.setC2sSpi(elements[23]);
            espAndAhExtension.setS2cSpi("");
            data.setEspAndAhExtension(espAndAhExtension);
        }
        data.setKey();
    }
}
