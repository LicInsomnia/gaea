package com.tincery.gaea.source.pptpandl2tp.execute;

import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.stereotype.Component;

@Component
public class PptpAndL2TPLineSupport extends SrcLineSupport {

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
     * @param data       数据实体
     */
    public void set7Tuple(String serverMac,
                          String clientMac,
                          String serverIp,
                          String clientIp,
                          String serverPort,
                          String clientPort,
                          String protocol,
                          AbstractMetaData data) throws NumberFormatException {
        data.setServerMac(null == serverMac ? null : serverMac.toUpperCase())
                .setClientMac(null == clientMac ? null : clientMac.toUpperCase())
                .setServerIp(NetworkUtil.arrangeIp(serverIp))
                .setClientIp(NetworkUtil.arrangeIp(clientIp))
                .setServerPort(Integer.parseInt(serverPort))
                .setClientPort(Integer.parseInt(clientPort))
                .setProtocol(Integer.parseInt(protocol));
        if (data.getServerPort() == 1723) {
            data.setProName(HeadConst.PRONAME.PPTP);
        } else {
            data.setProName(HeadConst.PRONAME.L2TP);
        }
    }


}
