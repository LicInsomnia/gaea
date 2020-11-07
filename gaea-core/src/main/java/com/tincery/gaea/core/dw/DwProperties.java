package com.tincery.gaea.core.dw;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gxz gongxuanzhang@foxmail.com Src层通用配置
 **/
@Setter
@Getter
public class DwProperties {

    /**
     * 多线程数
     */
    private int executor = 0;

    /**
     * 执行延迟时间
     */
    private int delayExecutionTime = 30;

}
