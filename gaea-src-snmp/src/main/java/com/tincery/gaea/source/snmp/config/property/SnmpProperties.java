package com.tincery.gaea.source.snmp.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(SnmpProperties.PREFIX)
@Component
@Getter
@Setter
public class SnmpProperties extends SrcProperties {

    /**
     * Prefix of {@link SnmpProperties}.
     */
    public static final String PREFIX = "src.snmp";


}
