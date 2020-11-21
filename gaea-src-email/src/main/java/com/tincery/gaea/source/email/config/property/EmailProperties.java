package com.tincery.gaea.source.email.config.property;

import com.sun.jmx.snmp.defaults.SnmpProperties;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(EmailProperties.PREFIX)
@Component
@Getter
@Setter
public class EmailProperties extends SrcProperties {

    /**
     * Prefix of {@link EmailProperties}.
     */
    public static final String PREFIX = "src.email";


}
