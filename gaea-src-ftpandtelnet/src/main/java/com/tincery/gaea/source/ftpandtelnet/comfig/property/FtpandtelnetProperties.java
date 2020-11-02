package com.tincery.gaea.source.ftpandtelnet.comfig.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(FtpandtelnetProperties.PREFIX)
@Component
@Getter
@Setter
public class FtpandtelnetProperties extends SrcProperties {

    /**
     * Prefix of {@link FtpandtelnetProperties}.
     */
    public static final String PREFIX = "src.ftpandtelnet";


}
