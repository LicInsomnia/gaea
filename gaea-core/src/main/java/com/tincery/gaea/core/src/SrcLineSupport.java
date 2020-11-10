package com.tincery.gaea.core.src;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SrcLineSupport {

    @Autowired
    public IpChecker ipChecker;
    @Autowired
    private ApplicationProtocol applicationProtocol;
    @Autowired
    private PayloadDetector payloadDetector;
    @Autowired
    private GroupGetter groupGetter;

    /**
     * 通过查询application_protocol表获取协议名
     *
     * @param key  协议_端口拼接（protocol_serverPort）
     * @param data src数据实体
     * @return 是否查询到ProName
     */
    public boolean setProName(String key, AbstractMetaData data) {
        ApplicationInformationBO application = this.applicationProtocol.getApplication(key);
        if (application == null) {
            return false;
        }
        String proName = application.getProName();
        if (StringUtils.isNotEmpty(proName)) {
            data.setProName(proName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据src层中targetName信息为实体中targetName赋值
     *
     * @param targetName
     * @param data
     */
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

    public void setTime(Long capTimeN, Long endTimeN, AbstractSrcData data) {
        data.setCapTime(DateUtils.validateTime(capTimeN));
        data.setDuration(endTimeN - capTimeN);
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
        data.setServerMac(null == serverMac ? null : serverMac.toUpperCase())
                .setClientMac(null == clientMac ? null : clientMac.toUpperCase())
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

    /**
     * 设置移动终端属性
     *
     * @param imsi   IMSI
     * @param imei   IMEI
     * @param msisdn MSISDN
     * @param data   数据实体
     */
    public void setMobileElements(String imsi, String imei, String msisdn, AbstractSrcData data) {
        data.setImsi(SourceFieldUtils.parseStringStrEmptyToNull(imsi))
                .setImei(SourceFieldUtils.parseStringStrEmptyToNull(imei))
                .setMsisdn(SourceFieldUtils.parseStringStrEmptyToNull(msisdn));
    }

    /**
     * 设置会话ID
     *
     * @param userId   客户端ID
     * @param serverId 服务端ID
     * @param data     数据实体
     */
    public void setPartiesId(String userId, String serverId, AbstractSrcData data) {
        data.setUserId(SourceFieldUtils.parseStringStrEmptyToNull(userId))
                .setServerId(SourceFieldUtils.parseStringStrEmptyToNull(serverId));
    }

    /**
     * 设置外层五元组
     *
     * @param clientIpOuter   外层客户端IP
     * @param serverIpOuter   外层服务端IP
     * @param clientPortOuter 外层客户端端口
     * @param serverPortOuter 外层服务端端口
     * @param protocolOuter   外层协议
     * @param data            数据实体
     */
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
        data.setMalformedUpPayload((null == upPayload || "0000000000000000000000000000000000000000".equals(upPayload)) ? "" : upPayload.toLowerCase())
                .setMalformedDownPayload((null == downPayload || "0000000000000000000000000000000000000000".equals(downPayload)) ? "" : downPayload.toLowerCase())
                .setProName(this.payloadDetector.getProName(data));
    }

    public boolean isInnerIp(String ipDecStr) {
        return this.ipChecker.isInner(Long.parseLong(ipDecStr));
    }

    public boolean isForeign(String ip) {
        return this.ipChecker.isForeign(ip);
    }


    /**
     * 判断是否是服务端
     * @param elements 判断依据1. 数据中是否有D2S Client等字符串出现
     * @param index 从数组何处开始循环
     * @param data 此处的data需要装载dataType  和 serverPort(为srcPort) clientPort(为dstPort) 这里的这两个属性是临时装填的  后面会根据情况被替换
     * @return boolean D2S Client = false  S2D Client = true;
     */
    public Boolean judgeisD2SServer(String[] elements,Integer index,AbstractSrcData data) throws Exception {
        Boolean isServer = null;
        Integer dataType = data.getDataType();
        if (dataType>=0){
            for (int i = index; i < elements.length; i++) {
                if (StringUtils.isEmpty(elements[i])) {
                    continue;
                }
                //第一个判断依据 数据中是否有符合标准的数据
                isServer = sureIsD2SServerByElements(elements[i]);
                if (Objects.nonNull(isServer)){
                    System.out.println(isServer);
                    return isServer;
                }
            }
        }

        return isServer;
    }

    /**
     * 临时装载以下属性 为了判断是否是服务端
     * @param data 被装载的对象
     * @param dataType int
     * @param srcPort String->int 对应ServerPort
     * @param dstPort String->int 对应ClientPort
     */
    public void fixForJudgeIsServer(AbstractSrcData data,Integer dataType,String srcPort,String dstPort){
        data.setDataType(dataType)
                .setServerPort(Integer.parseInt(srcPort))
                .setClientPort(Integer.parseInt(dstPort));
    }
    /**
     * 该方法是确定isServer关键变量的值的。。因为要遍历所有的握手信息才能获得该变量值，然后根据该变量去装填其他属性
     * @param element 根据该元素判断
     * @return isServer
     */
    public Boolean sureIsD2SServerByElements(String element) throws Exception {
        Boolean isD2SServer = null;
        String[] kv = element.split(":");
        if (kv.length != 2) {
            throw new Exception("握手会话数据格式有误...");
        }
        // 该数据形如 D2S Client Hello || S2D Server Hello || Apllication...
        String start = kv[0];
        if (start.startsWith("S2D Client")){
            isD2SServer = true;
        }else if (start.startsWith("D2S Client")){
            isD2SServer = false;
        }else if (start.startsWith("S2D Server")){
            isD2SServer = false;
        }else if (start.startsWith("D2S Server")){
            isD2SServer = true;
        }
        return isD2SServer;
    }

    /**
     * 判断依据二  根据端口和dataType判断
     */
    public Boolean sureisD2SServerByPortAndDataType(Integer dataType,Integer srcPort,Integer dstPort , Boolean isD2SServer){
        if (!Objects.equals(-1,dataType)){
            if ( Objects.equals(1194,srcPort) && (!Objects.equals(1194,dstPort))){
                isD2SServer = false;
            }else if ((!Objects.equals(1194,srcPort)) && Objects.equals(1194,dstPort)){
                isD2SServer = true;
            }
        }
        return isD2SServer;
    }

    /**
     * 判断依据三  根据内外网ip地址 判断
     */
    public Boolean sureisD2SServerByIsInnerIp(String srcIp,String dstIp,Boolean isD2SServer){
        boolean srcInner = isInnerIp(srcIp);
        boolean dstInner = isInnerIp(dstIp);
        if (srcInner && (!dstInner)){
            isD2SServer = true;
        }else if ((!srcInner) && dstInner){
            isD2SServer = false;
        }
        return isD2SServer;
    }

    /**
     * 判断依据四 根据端口大小判断
     */
    public Boolean sureisD2SServerByComparePort(Integer serverPort,Integer clientPort){
        return serverPort > clientPort;
    }
}
