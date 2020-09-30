package com.tincery.gaea.source.ssl.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties (SslProperties.PREFIX)
@Component
@Getter
@Setter
public class SslProperties extends SrcProperties {

    /**
     * Prefix of {@link SslProperties}.
     */
    public static final String PREFIX = "src.ssl";


}
