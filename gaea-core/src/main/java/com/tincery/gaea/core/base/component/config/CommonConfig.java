package com.tincery.gaea.core.base.component.config;

import com.tincery.gaea.api.base.Location;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

@ConfigurationProperties(prefix = "common")
@Component
@Data
@Slf4j
public class CommonConfig {
    private Authentication authentication;
    private EmailConfig email;
    private Set<String> cerKeys;
    private Location dflocation;


    @Setter@Getter
    public static class Authentication{
        private Date authorizationDate;
        private boolean perpetualLicense;
        private List<String> processorIdList;
    }
    @Setter@Getter
    public static class EmailConfig{
        private AttchAlertInfo attchAlertinfo;
        private boolean perpetualLicense;
        private List<String> whiteSuffix;
    }

    @Setter@Getter
    public static class AttchAlertInfo{
        private String categoryDesc;
        private String subcategoryDesc;
        private Integer level;
    }

}
