package com.tincery.gaea.source.ssl.config.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tincery.gaea.core.base.component.AbstractSrcCommonProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(SslProperties.PREFIX)
@Component
@Getter
@Setter
public class SslProperties extends AbstractSrcCommonProperties {

    /**
     * Prefix of {@link SslProperties}.
     */
    public static final String PREFIX = "src.ssl";

    private double dgaValue;

    @Autowired
    @JsonIgnore
    private Environment environment;


    @PostConstruct
    public void init() {
        String category = environment.getProperty("category");
        this.setCategory(category);
    }


}
