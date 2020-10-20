package com.tincery.gaea.core.src;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SrcLineSupport {

    @Autowired
    private ApplicationProtocol applicationProtocol;

    @Autowired
    private PayloadDetector payloadDetector;

    @Autowired
    private GroupGetter groupGetter;

    public ApplicationInformationBO getApplication(String key) {
        return applicationProtocol.getApplication(key);
    }

    public String getTargetName(String targetName) {
        return this.groupGetter.getGroupName(targetName);
    }

    public void set7Tuple(String serverMac,
                          String clientMac,
                          String serverIp,
                          String clientIp,
                          String serverPort,
                          String clientPort,
                          String protocol,
                          String proName,
                          AbstractMetaData data) {
        data.setServerMac(serverMac);
        data.setClientMac(clientMac);
        data.setServerIp(NetworkUtil.arrangeIp(serverIp));
        data.setClientIp(NetworkUtil.arrangeIp(clientIp));
        data.setServerPort(Integer.parseInt(serverPort));
        data.setClientPort(Integer.parseInt(clientPort));
        data.setProtocol(Integer.parseInt(protocol));
        data.setProName(proName);
    }

    public void setFlow(String upPkt, String upByte, String downPkt, String downByte, AbstractMetaData data) {
        data.setUpPkt(Long.parseLong(upPkt));
        data.setUpByte(Long.parseLong(upByte));
        data.setDownPkt(Long.parseLong(downPkt));
        data.setDownByte(Long.parseLong(downByte));
    }

    public void setMalformedPayload(String upPayload, String downPayload, AbstractMetaData data) {
        data.setMalformedUpPayload("0000000000000000000000000000000000000000".equals(upPayload) ? "" : upPayload);
        data.setMalformedDownPayload("0000000000000000000000000000000000000000".equals(downPayload) ? "" : downPayload);
        data.setProName(payloadDetector.getProName(data));
    }
}
