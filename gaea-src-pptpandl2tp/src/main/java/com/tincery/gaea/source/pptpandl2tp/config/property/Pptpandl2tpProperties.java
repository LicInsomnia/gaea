package com.tincery.gaea.source.pptpandl2tp.config.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(Pptpandl2tpProperties.PREFIX)
@Component
@Getter
@Setter
public class Pptpandl2tpProperties extends SrcProperties {

    /**
     * Prefix of {@link Pptpandl2tpProperties}.
     */
    public static final String PREFIX = "src.pptpandl2tp";


}
