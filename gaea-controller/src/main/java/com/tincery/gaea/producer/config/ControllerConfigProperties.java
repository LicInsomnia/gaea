package com.tincery.gaea.producer.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "controller.model")
@Getter
@Setter
@Component
public class ControllerConfigProperties {

    private SrcConfig src;
    private DataWarehouseConfig datawarehouse;


    @Setter
    @Getter
    public static class SrcConfig {
        private String flow;
        private String session;
        private String impsession;
        private String ssl;
        private String email;
        private String http;
        private String dns;
        private String ssh;
    }

    @Setter
    @Getter
    public static class DataWarehouseConfig {
        private String reorganization;
    }




}
