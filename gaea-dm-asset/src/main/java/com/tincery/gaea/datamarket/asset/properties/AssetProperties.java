package com.tincery.gaea.datamarket.asset.properties;

import com.tincery.gaea.core.dm.DmProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@ConfigurationProperties(AssetProperties.PREFIX)
@Component
@Getter
@Setter
public class AssetProperties extends DmProperties {

    /**
     * Prefix of {@link AssetProperties}.
     */
    public static final String PREFIX = "dm.asset";

}
