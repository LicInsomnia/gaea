package com.tincery.gaea.source.impsession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author Administrator
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class GaeaSourceImpSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaeaSourceImpSessionApplication.class, args);
    }

}
