package com.tincery.gaea.datamarket.alarmcombine.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(AlarmCombineProperties.PREFIX)
@Component
@Getter
@Setter
public class AlarmCombineProperties{

    /**
     *  1. 安全系统
     *  2. ZC系统
     */
    private int secure = 0;
    /**
     * Prefix of {@link AlarmCombineProperties}.
     */
    public static final String PREFIX = "dm.alarmcombine";

}
