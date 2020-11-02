package com.tincery.gaea.source.isakmp.execute;

import com.tincery.gaea.api.src.IsakmpData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.stereotype.Component;

@Component
public class IsakmpLineSupport extends SrcLineSupport {

    void set7TupleAndFlow(boolean s2dFlg, String sMac, String dMac, String sIp, String dIp, String sPort, String dPort,
                          String sPkt, String sByte, String dPkt, String dByte, IsakmpData data) throws NumberFormatException {
        if (s2dFlg) {
            this.set7Tuple(dMac, sMac, dIp, sIp, dPort, sPort, "17", HeadConst.PRONAME.ISAKMP, data);
            this.setFlow(sPkt, sByte, dPkt, dByte, data);
        } else {
            this.set7Tuple(sMac, dMac, sIp, dIp, sPort, dPort, "17", HeadConst.PRONAME.ISAKMP, data);
            this.setFlow(dPkt, dByte, sPkt, sByte, data);
        }
    }

}
