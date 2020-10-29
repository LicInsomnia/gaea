package com.tincery.gaea.source.openven.conifg.property;

import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(OpenVpnProperties.PREFIX)
@Component
@Getter
@Setter
public class OpenVpnProperties extends SrcProperties {

    /**
     * Prefix of {@link OpenVpnProperties}.
     */
    public static final String PREFIX = "src.openvpn";

}
