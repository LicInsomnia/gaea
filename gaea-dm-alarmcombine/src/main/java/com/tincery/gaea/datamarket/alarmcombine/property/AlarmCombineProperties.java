package com.tincery.gaea.datamarket.alarmcombine.property;

import com.tincery.gaea.core.dm.DmProperties;
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
public class AlarmCombineProperties extends DmProperties {

    /**
     * Prefix of {@link AlarmCombineProperties}.
     */
    public static final String PREFIX = "dm.alarmcombine";

}
