package com.tincery.gaea.source.espandah.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(EspAndAhProperties.PREFIX)
@Component
@Getter
@Setter
public class EspAndAhProperties extends SrcProperties {

    /**
     * Prefix of {@link EspAndAhProperties}.
     */
    public static final String PREFIX = "src.espandah";


}
