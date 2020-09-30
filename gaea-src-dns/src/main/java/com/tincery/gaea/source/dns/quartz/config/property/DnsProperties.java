package com.tincery.gaea.source.dns.quartz.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties (DnsProperties.PREFIX)
@Component
@Getter
@Setter
public class DnsProperties extends SrcProperties {

    /**
     * Prefix of {@link DnsProperties}.
     */
    public static final String PREFIX = "src.dns";

}
