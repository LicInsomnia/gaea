package com.tincery.gaea.api.dm;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 协议组
 * 一个协议组包含一个协议类型和 0-多个端口
 **/
@Setter
@Getter
public class ProtocolGroup extends SimpleBaseDO {
    /***协议类型*/
    private int type;
    private List<Integer> ports;

    public boolean checkProtocolAndPort(int protocol, int port) {
        if (type != protocol) {
            return false;
        }
        if (CollectionUtils.isEmpty(ports)) {
            return true;
        }
        return ports.contains(port);
    }

}
