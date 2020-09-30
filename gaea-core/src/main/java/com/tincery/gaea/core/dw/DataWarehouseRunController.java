package com.tincery.gaea.core.dw;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Component
public class DataWarehouseRunController {

    private static MongoTemplate mongoTemplate;

    @Resource (name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    public static JSONObject getRunConfig(String category) {
        Query query = new Query(Criteria.where("_id").is(NodeInfo.getNodeName()));
        JSONObject runConfig = mongoTemplate.findOne(query, JSONObject.class, "run_config");
        return new JSONObject((Map) runConfig.get(category));
    }

    public static void reWriteRunconfig(String category, JSONObject jsonObject) {
        Query query = new Query(Criteria.where("_id").is(NodeInfo.getNodeName()));
        Update update = new Update();
        update.set(category, jsonObject);
        mongoTemplate.updateFirst(query, update, "run_config");
    }

    @PostConstruct
    public void init() {
        DataWarehouseRunController.mongoTemplate = this.sysMongoTemplate;
    }

}
