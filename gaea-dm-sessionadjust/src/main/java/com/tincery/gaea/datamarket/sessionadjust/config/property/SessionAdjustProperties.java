package com.tincery.gaea.datamarket.sessionadjust.config.property;

import com.tincery.gaea.core.dm.DmProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Insomnia
 */
@ConfigurationProperties(SessionAdjustProperties.PREFIX)
@Component
@Getter
@Setter
public class SessionAdjustProperties extends DmProperties {

    /**
     * Prefix of {@link SessionAdjustProperties}.
     */
    public static final String PREFIX = "dm.sessionadjust";
}
