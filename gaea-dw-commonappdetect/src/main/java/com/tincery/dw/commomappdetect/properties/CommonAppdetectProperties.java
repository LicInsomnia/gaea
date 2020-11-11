package com.tincery.dw.commomappdetect.properties;

import com.tincery.gaea.core.dw.DwProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(CommonAppdetectProperties.PREFIX)
@Component
@Getter
@Setter
public class CommonAppdetectProperties extends DwProperties {

    /**
     * Prefix of {@link CommonAppdetectProperties}.
     */
    public static final String PREFIX = "dw.commonappdetect";

}
