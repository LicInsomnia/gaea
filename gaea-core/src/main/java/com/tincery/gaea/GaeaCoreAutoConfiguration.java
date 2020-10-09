package com.tincery.gaea;

import com.tincery.gaea.core.base.component.support.CommonConfigInit;
import com.tincery.gaea.core.base.component.support.GaeaPropertiesInit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
@ComponentScans (
        {@ComponentScan ("com.tincery.gaea.core.*")
        })
public class GaeaCoreAutoConfiguration {

    @Bean
    public GaeaPropertiesInit getEnvInit() {
        return new GaeaPropertiesInit();
    }

    @Bean
    public CommonConfigInit getCommonConfigInit() {
        return new CommonConfigInit();
    }

}
