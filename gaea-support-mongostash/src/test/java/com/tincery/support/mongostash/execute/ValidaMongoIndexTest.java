package com.tincery.support.mongostash.execute;

import com.tincery.support.mongostash.MongoStashApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;


@SpringBootTest(classes = MongoStashApplication.class)
class ValidaMongoIndexTest {

    @Resource(name="sysMongoTemplate")
    private MongoTemplate mongoTemplate;


}
