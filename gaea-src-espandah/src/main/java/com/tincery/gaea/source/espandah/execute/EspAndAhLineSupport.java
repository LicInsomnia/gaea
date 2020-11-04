package com.tincery.gaea.source.espandah.execute;

import com.tincery.gaea.api.src.EspAndAhData;
import com.tincery.gaea.api.src.extension.EspAndAhExtension;
import com.tincery.gaea.core.src.SrcLineSupport;

public class EspAndAhLineSupport extends SrcLineSupport {

    public void setCommon(String sMac,
                          String dMac,
                          String sIp,
                          String dIp,
                          String sPort,
                          String dPort,
                          String protocol,
                          String proName,
                          String pkt,
                          String byteNum,
                          String payload,
                          EspAndAhData data
    ) throws NumberFormatException {
        EspAndAhExtension espAndAhExtension = new EspAndAhExtension();
        if (sIp.length() == 32 || dIp.length() == 32) {
            super.set7Tuple(sMac, dMac, sIp, dIp, sPort, dPort, protocol, proName, data);
            super.setFlow(pkt, "0", byteNum, "0", data);
            espAndAhExtension.setUpPayload(payload);
            data.setEspAndAhExtension(espAndAhExtension);
            return;
        }
        long sIpN = Long.parseLong(sIp);
        long dIpN = Long.parseLong(dIp);
        if (sIpN >= dIpN) {
            super.set7Tuple(sMac, dMac, sIp, dIp, sPort, dPort, protocol, proName, data);
            super.setFlow("0", pkt, "0", byteNum, data);
            espAndAhExtension.setUpPayload(payload);
            data.setEspAndAhExtension(espAndAhExtension);
        } else {
            super.set7Tuple(dMac, sMac, dIp, sIp, dPort, sPort, protocol, proName, data);
            super.setFlow(pkt, "0", byteNum, "0", data);
            espAndAhExtension.setDownPayload(payload);
            data.setEspAndAhExtension(espAndAhExtension);
        }
    }
}
