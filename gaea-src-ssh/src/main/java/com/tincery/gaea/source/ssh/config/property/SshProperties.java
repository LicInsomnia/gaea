package com.tincery.gaea.source.ssh.config.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(SshProperties.PREFIX)
@Component
@Getter
@Setter
public class SshProperties extends SrcProperties {

    /**
     * Prefix of {@link SshProperties}.
     */
    public static final String PREFIX = "src.ssh";


}
