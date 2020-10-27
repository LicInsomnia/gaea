package com.tincery.gaea.api.dm;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * IP组
 * 包含一个IP或者一个IP段
 * IP对应一个或多个协议组
 * 一个协议组包含一个协议和 0到多个端口
 *
 **/
@Setter
@Getter
public class IpGroup extends SimpleBaseDO {
    private boolean unique;
    private Long minIp;
    private Long maxIp;
    private List<ProtocolGroup> protocols;


}
