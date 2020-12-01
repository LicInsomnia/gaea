package com.tincery.gaea.datamarket.alarmstatistic.property;

import com.tincery.gaea.core.dm.DmProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(AlarmStatisticProperties.PREFIX)
@Component
@Getter
@Setter
public class AlarmStatisticProperties extends DmProperties {

    private int limit;
    /**
     * Prefix of {@link AlarmStatisticProperties}.
     */
    public static final String PREFIX = "dm.alarmstatistic";

}
