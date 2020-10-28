package com.tincery.gaea.datawarehouse.reorganization;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication()
@EnableDiscoveryClient
public class GaeaDwReorganizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(GaeaDwReorganizationApplication.class, args);
    }
}

