package com.tincery.gaea.datawarehouse.reorganization.config;

import com.tincery.gaea.core.dw.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 */
@Configuration
public class ReorganizationConfig {

    @Bean
    public SessionFactory getSessionFactory() {
        return new SessionFactory();
    }

}

