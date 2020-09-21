package com.tincery.gaea.source.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class GaeaSourceSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaeaSourceSessionApplication.class, args);
    }

}
