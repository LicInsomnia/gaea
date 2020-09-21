package com.tincery.gaea.core.base.dao;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AppRule;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : gxz
 * @date :  2019-12-17 11:11:59
**/


@Repository
public class AppRuleDao extends SimpleBaseDaoImpl<AppRule> {




    @Override
    protected Class<AppRule> getClazz() {
    return AppRule.class;
    }


    @Override
    @Value("apprule")
    public void setDbName(String dbName) {
        this.dbName =  dbName;
    }


    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<JSONObject> findAllJSON(){
        return this.mongoTemplate.findAll(JSONObject.class, this.getDbName());
    }


}
