package com.tincery.gaea.api.base;

import com.tincery.gaea.api.dm.ProtocolGroup;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * IP组
 * 包含一个IP或者一个IP段
 * IP对应一个或多个协议组
 * 一个协议组包含一个协议和 0到多个端口
 **/
@Setter
@Getter
public class IpGroup extends SimpleBaseDO implements IpHitable {
    private boolean unique;
    private Long minIp;
    private Long maxIp;
    private List<ProtocolGroup> protocols;

    @Override
    public boolean hit(long ip, int protocol, int port) {
        if (!checkIp(ip)) {
            return false;
        }
        if (CollectionUtils.isEmpty(protocols)) {
            return false;
        }
        return protocols.stream().anyMatch(protocolGroup -> protocolGroup.checkProtocolAndPort(protocol,port));
    }

    /****
     * IP是否符合规则
     * @param ip ip
     * @return boolean 是否符合
     **/
    private boolean checkIp(long ip) {
        if (unique) {
            return Objects.equals(minIp, ip);
        } else {
            return ip >= minIp && ip <= maxIp;
        }
    }



}
