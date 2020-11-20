package com.tincery.gaea.datawarehouse.reorganization.config.property;

import com.tincery.gaea.core.dw.DwProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(ReorganizationProperties.PREFIX)
@Component
@Getter
@Setter
public class ReorganizationProperties extends DwProperties {

    /**
     * Prefix of {@link ReorganizationProperties}.
     */
    public static final String PREFIX = "dw.reorganization";

}
