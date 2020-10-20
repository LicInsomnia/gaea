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

    /**
     * 通过查询application_protocol表获取协议名
     *
     * @param key 协议_端口拼接（protocol_serverPort）
     * @return 协议名，若未查询到则返回null
     */
    public ApplicationInformationBO getApplication(String key) {
        return applicationProtocol.getApplication(key);
    }

    /**
     * 根据目标名查表获得目标分组名
     *
     * @param targetName 数据中目标名
     * @return 分组名
     */
    public String getGroupName(String targetName) {
        return this.groupGetter.getGroupName(targetName);
    }

    /**
     * 设置七元组
     *
     * @param serverMac
     * @param clientMac
     * @param serverIp
     * @param clientIp
     * @param serverPort
     * @param clientPort
     * @param protocol
     * @param proName
     * @param data
     */
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

    /**
     * 设置流量
     *
     * @param upPkt
     * @param upByte
     * @param downPkt
     * @param downByte
     * @param data
     */
    public void setFlow(String upPkt, String upByte, String downPkt, String downByte, AbstractMetaData data) {
        data.setUpPkt(Long.parseLong(upPkt));
        data.setUpByte(Long.parseLong(upByte));
        data.setDownPkt(Long.parseLong(downPkt));
        data.setDownByte(Long.parseLong(downByte));
    }

    /**
     * 设置malformed载荷信息
     *
     * @param upPayload
     * @param downPayload
     * @param data
     */
    public void setMalformedPayload(String upPayload, String downPayload, AbstractMetaData data) {
        data.setMalformedUpPayload("0000000000000000000000000000000000000000".equals(upPayload) ? "" : upPayload);
        data.setMalformedDownPayload("0000000000000000000000000000000000000000".equals(downPayload) ? "" : downPayload);
        data.setProName(payloadDetector.getProName(data));
    }
}
