package com.tincery.gaea.source.qq.config.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(QQProperties.PREFIX)
@Component
@Getter
@Setter
public class QQProperties extends SrcProperties {

    /**
     * Prefix of {@link QQProperties}.
     */
    public static final String PREFIX = "src.qq";
}
