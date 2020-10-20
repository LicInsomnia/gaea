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
     * @param key 协议_端口拼接（protocol_serverPort）
     * @return 协议名，若未查询到则返回null
     */
    public ApplicationInformationBO getApplication(String key) {
        return applicationProtocol.getApplication(key);
    }

    /**
     * 根据目标名查表获得目标分组名
     * @param targetName 数据中目标名
     * @return 分组名
     */
    public String getGroupName(String targetName) {
        return this.groupGetter.getGroupName(targetName);
    }

    /**
     * 设置七元组
     *
     * @param serverMac  服务端MAC地址
     * @param clientMac  客户端MAC地址
     * @param serverIp   服务端IP
     * @param clientIp   客户端IP
     * @param serverPort 服务端端口
     * @param clientPort 客户端端口
     * @param protocol   协议
     * @param proName    协议名
     * @param data       数据实体
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
     * @param upPkt 上行字节数
     * @param upByte 上行包数
     * @param downPkt 下行字节数
     * @param downByte 下行包数
     * @param data 数据实体
     */
    public void setFlow(String upPkt, String upByte, String downPkt, String downByte, AbstractMetaData data) {
        data.setUpPkt(Long.parseLong(upPkt));
        data.setUpByte(Long.parseLong(upByte));
        data.setDownPkt(Long.parseLong(downPkt));
        data.setDownByte(Long.parseLong(downByte));
    }

    /**
     * 设置malformed载荷信息
     * @param upPayload 上行载荷
     * @param downPayload 下行载荷
     * @param data 数据实体
     */
    public void setMalformedPayload(String upPayload, String downPayload, AbstractMetaData data) {
        data.setMalformedUpPayload("0000000000000000000000000000000000000000".equals(upPayload) ? "" : upPayload);
        data.setMalformedDownPayload("0000000000000000000000000000000000000000".equals(downPayload) ? "" : downPayload);
        data.setProName(payloadDetector.getProName(data));
    }
}
