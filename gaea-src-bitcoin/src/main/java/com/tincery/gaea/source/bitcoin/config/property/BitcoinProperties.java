package com.tincery.gaea.source.bitcoin.config.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(BitcoinProperties.PREFIX)
@Component
@Getter
@Setter
public class BitcoinProperties extends SrcProperties {

    /**
     * Prefix of {@link BitcoinProperties}.
     */
    public static final String PREFIX = "src.bitcoin";
}
