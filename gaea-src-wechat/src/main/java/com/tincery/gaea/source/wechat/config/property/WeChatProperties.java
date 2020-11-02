package com.tincery.gaea.source.wechat.config.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(WeChatProperties.PREFIX)
@Component
@Getter
@Setter
public class WeChatProperties extends SrcProperties {

    /**
     * Prefix of {@link WeChatProperties}.
     */
    public static final String PREFIX = "src.wechat";


}
