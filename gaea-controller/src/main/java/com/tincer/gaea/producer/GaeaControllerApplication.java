package com.tincer.gaea.producer;

import com.tincery.gaea.GaeaCoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication (exclude = GaeaCoreAutoConfiguration.class)
@EnableJms
public class GaeaControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaeaControllerApplication.class, args);
    }


}
