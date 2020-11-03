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

    private SourceConfig source;
    private DataWarehouseConfig dataWarehouse;

    @Setter
    @Getter
    public static class SourceConfig {
        private String flow;
        private String session;
        private String impSession;
        private String ssl;
        private String openVpn;
        private String email;
        private String http;
        private String dns;
        private String ssh;
        private String pptpandl2tp;
        private String wechat;
        private String ftpandtelnet;
    }

    @Setter
    @Getter
    public static class DataWarehouseConfig {
        private String reorganization;
    }

}
