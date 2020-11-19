package com.tincery.gaea.core.dm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com Dm层通用配置
 **/
@Setter
@Getter
@Component
public class DmProperties {

    /**
     * a
     * 多线程数
     */
    private int executor = 0;

    private boolean back = false;

    private boolean test = false;

    /**
     * 1. 安全系统
     * 2. ZC系统
     */
    private int secure = 0;

}
