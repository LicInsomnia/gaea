package com.tincery.gaea.source.impsession.config.property;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tincery.gaea.core.src.SrcProperties;
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
@ConfigurationProperties (ImpSessionProperties.PREFIX)
@Component
@Getter
@Setter
public class ImpSessionProperties extends SrcProperties {

    /**
     * Prefix of {@link ImpSessionProperties}.
     */
    public static final String PREFIX = "src.impsession";

    @Autowired
    @JsonIgnore
    private Environment environment;


    @PostConstruct
    public void init() {
        String category = environment.getProperty("category");
        this.setCategory(category);

    }


}
