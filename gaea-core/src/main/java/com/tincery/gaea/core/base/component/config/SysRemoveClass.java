package com.tincery.gaea.core.base.component.config;

import com.tincery.starter.condition.SysMongoTemplateCondition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import javax.annotation.Resource;

/**
 * @author gxz
 */
@Configuration
@Conditional(SysMongoTemplateCondition.class)
public class SysRemoveClass implements ApplicationListener<ContextRefreshedEvent> {

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        MongoConverter proconverter = sysMongoTemplate.getConverter();
        if (proconverter.getTypeMapper().isTypeKey("_class")) {
            ((MappingMongoConverter) proconverter).setTypeMapper(new DefaultMongoTypeMapper(null));
        }

    }
}
