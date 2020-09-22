package com.tincery.gaea.source.email.config.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tincery.gaea.core.src.AbstractSrcProperties;
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
@ConfigurationProperties(EmailProperties.PREFIX)
@Component
@Getter
@Setter
public class EmailProperties extends AbstractSrcProperties {

    /**
     * Prefix of {@link EmailProperties}.
     */
    public static final String PREFIX = "src.dns";

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
