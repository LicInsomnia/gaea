package com.tincery.gaea.datawarehouse.reorganization.config;

import com.tincery.gaea.core.dw.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReorgnizationConfig {

    @Bean
    public SessionFactory getSessionFactory() {
        return new SessionFactory();
    }
}

