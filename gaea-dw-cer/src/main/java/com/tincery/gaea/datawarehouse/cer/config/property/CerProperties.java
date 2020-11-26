package com.tincery.gaea.datawarehouse.cer.config.property;


import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author lbz liubangzhou@163.com
 **/
@ConfigurationProperties(CerProperties.PREFIX)
@Component
@Getter
@Setter
public class CerProperties {

    /**
     * Prefix of {@link CerProperties}.
     */
    public static final String PREFIX = "dw.cer";

    private List<Document> defaultAlarm;
    private Document gmConfig;
    private Document defaultConfig;
    private List<Document> webcheck;

    /***缓存最大xxx行记录输出一次csv*/
    private int maxLine = 30000;
}
