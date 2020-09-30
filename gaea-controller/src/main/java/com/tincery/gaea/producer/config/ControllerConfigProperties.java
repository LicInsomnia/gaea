package com.tincery.gaea.producer.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties (prefix = "controller")
@Data
public class ControllerConfigProperties {

    private boolean email;

}
