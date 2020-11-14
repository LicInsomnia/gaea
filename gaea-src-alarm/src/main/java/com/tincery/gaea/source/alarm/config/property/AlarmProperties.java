package com.tincery.gaea.source.alarm.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(AlarmProperties.PREFIX)
@Component
@Getter
@Setter
public class AlarmProperties extends SrcProperties {

    /**
     * Prefix of {@link AlarmProperties}.
     */
    public static final String PREFIX = "src.alarm";


}
