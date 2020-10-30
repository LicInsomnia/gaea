package com.tincery.gaea.source.isakmp.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(IsakmpProperties.PREFIX)
@Component
@Getter
@Setter
public class IsakmpProperties extends SrcProperties {

    /**
     * Prefix of {@link IsakmpProperties}.
     */
    public static final String PREFIX = "src.isakmp";


}
