package com.tincery.gaea.source.impsession.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(ImpSessionProperties.PREFIX)
@Component
@Getter
@Setter
public class ImpSessionProperties extends SrcProperties {

    /**
     * Prefix of {@link ImpSessionProperties}.
     */
    public static final String PREFIX = "src.impsession";


}
