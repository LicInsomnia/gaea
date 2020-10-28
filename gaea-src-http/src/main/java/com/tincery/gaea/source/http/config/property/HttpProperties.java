package com.tincery.gaea.source.http.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(HttpProperties.PREFIX)
@Component
@Getter
@Setter
public class HttpProperties extends SrcProperties {

    /**
     * Prefix of {@link HttpProperties}.
     */
    public static final String PREFIX = "src.http";


}
