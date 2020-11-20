package com.tincery.gaea.datamarket.sessionjournal.config.property;

import com.tincery.gaea.core.dm.DmProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Insomnia
 */
@ConfigurationProperties(SessionJournalProperties.PREFIX)
@Component
@Getter
@Setter
public class SessionJournalProperties extends DmProperties {

    /**
     * Prefix of {@link SessionJournalProperties}.
     */
    public static final String PREFIX = "dm.sessionjournal";
}
