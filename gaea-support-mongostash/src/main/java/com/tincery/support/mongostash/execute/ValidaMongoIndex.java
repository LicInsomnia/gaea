package com.tincery.support.mongostash.execute;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.tincery.gaea.core.base.dao.TableConfigDao;
import com.tincery.starter.base.model.TableConfig;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
@Slf4j
public class ValidaMongoIndex implements ApplicationListener<ApplicationReadyEvent> {


    @Resource(name="sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    @Resource(name="proMongoTemplate")
    private MongoTemplate proMongoTemplate;

    @Autowired
    private TableConfigDao tableConfigDao;



    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        tableConfigDao.findAll().stream().filter(tableConfig -> tableConfig.getIndex()!=null).forEach(this::validaAndCreateIndex);
    }

    public void validaAndCreateIndex(TableConfig tableConfig){
        MongoTemplate mongoTemplate = tableConfig.getType().contains("pro")?proMongoTemplate:sysMongoTemplate;
        Map<String, Integer> index = tableConfig.getIndex();
        MongoCollection<Document> collection = mongoTemplate.getCollection(tableConfig.getName());
        ListIndexesIterable<Document> documents = collection.listIndexes();
        Set<String> currentAllIndex = new HashSet<>();
        for (Document document : documents) {
            currentAllIndex.addAll(((Document)document.get("key")).keySet());
        }
        index.forEach((key,in)->{
           if(!currentAllIndex.contains(key)){
               log.info("表{},创建{}索引{}",tableConfig.getName(),key,in);
               collection.createIndex(new Document(key,in),new IndexOptions().background(false).name(tableConfig.getName()+"_"+key));
           }
        });

    }



}
