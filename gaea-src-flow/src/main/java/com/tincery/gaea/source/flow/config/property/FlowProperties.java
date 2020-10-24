package com.tincery.gaea.source.flow.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(FlowProperties.PREFIX)
@Component
@Getter
@Setter
public class FlowProperties extends SrcProperties {

    /**
     * Prefix of {@link FlowProperties}.
     */
    public static final String PREFIX = "src.flow";

}
