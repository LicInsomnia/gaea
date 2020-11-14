package com.tincery.gaea.source.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author gongxuanzhang
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class GaeaSourceAlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaeaSourceAlarmApplication.class, args);
    }

}
