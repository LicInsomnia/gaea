package com.tincery.gaea.core.dm;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gxz gongxuanzhang@foxmail.com Dm层通用配置
 **/
@Setter
@Getter
public class DmProperties {

    /**
     * 多线程数
     */
    private int executor = 0;

    private boolean back = false;


}
