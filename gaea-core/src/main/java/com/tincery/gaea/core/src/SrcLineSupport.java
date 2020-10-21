package com.tincery.gaea.core.src;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
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

    @Autowired
    private IpChecker ipChecker;

    /**
     * 通过查询application_protocol表获取协议名
     *
     * @param key 协议_端口拼接（protocol_serverPort）
     * @return 协议名，若未查询到则返回null
     */
    public ApplicationInformationBO getApplication(String key) {
        return this.applicationProtocol.getApplication(key);
    }

    public void setTargetName(String targetName, AbstractMetaData data) {
        if (StringUtils.isEmpty(targetName)) {
            return;
        }
        int flag = targetName.charAt(0);
        if (flag < '5') {
            return;
        }
        data.setTargetName(targetName.substring(1));
        data.setImp(true);
        switch (flag) {
            case 'l':
                data.setGroupName("l2tp");
                break;
            case 'p':
                data.setGroupName("pptp");
                break;
            case '6':
                data.setGroupName(data.getTargetName());
                break;
            default:
                break;
        }
    }

    /**
     * 根据目标名查表获得目标分组名
     *
     * @param data src数据实体
     */
    public void setGroupName(AbstractMetaData data) {
        data.setGroupName(this.groupGetter.getGroupName(data.getTargetName()));
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
                          AbstractMetaData data) throws NumberFormatException {
        data.setServerMac(serverMac)
                .setClientMac(clientMac)
                .setServerIp(NetworkUtil.arrangeIp(serverIp))
                .setClientIp(NetworkUtil.arrangeIp(clientIp))
                .setServerPort(Integer.parseInt(serverPort))
                .setClientPort(Integer.parseInt(clientPort))
                .setProtocol(Integer.parseInt(protocol))
                .setProName(proName);
    }

    /**
     * 设置流量
     *
     * @param upPkt    上行字节数
     * @param upByte   上行包数
     * @param downPkt  下行字节数
     * @param downByte 下行包数
     * @param data     数据实体
     */
    public void setFlow(String upPkt,
                        String upByte,
                        String downPkt,
                        String downByte,
                        AbstractMetaData data
    ) throws NumberFormatException {
        data.setUpPkt(Long.parseLong(upPkt))
                .setUpByte(Long.parseLong(upByte))
                .setDownPkt(Long.parseLong(downPkt))
                .setDownByte(Long.parseLong(downByte));
    }

    public void set5TupleOuter(
            String clientIpOuter,
            String serverIpOuter,
            String clientPortOuter,
            String serverPortOuter,
            String protocolOuter,
            AbstractMetaData data
    ) throws NumberFormatException {
        // 若为0则该字段无效，强制写null
        data.setClientIpOuter(SourceFieldUtils.parseStringStr(clientIpOuter))
                .setServerIpOuter(SourceFieldUtils.parseStringStr(serverIpOuter))
                .setClientPortOuter(SourceFieldUtils.parseIntegerStr(clientPortOuter))
                .setServerPortOuter(SourceFieldUtils.parseIntegerStr(serverPortOuter))
                .setProtocolOuter(SourceFieldUtils.parseIntegerStr(protocolOuter));
    }

    /**
     * 设置malformed载荷信息
     *
     * @param upPayload   上行载荷
     * @param downPayload 下行载荷
     * @param data        数据实体
     */
    public void setMalformedPayload(String upPayload, String downPayload, AbstractMetaData data) {
        data.setMalformedUpPayload("0000000000000000000000000000000000000000".equals(upPayload) ? "" : upPayload)
                .setMalformedDownPayload("0000000000000000000000000000000000000000".equals(downPayload) ? "" : downPayload)
                .setProName(this.payloadDetector.getProName(data));
    }
}