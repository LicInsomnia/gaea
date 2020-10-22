package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class IpRange {
    private Long minIp;
    private Long maxIp;
}
