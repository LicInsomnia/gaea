package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.HttpApplicationRuleDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class HttpApplicationRuleDao extends SimpleBaseDaoImpl<HttpApplicationRuleDO> {


    @Override
    protected Class<HttpApplicationRuleDO> getClazz() {
        return HttpApplicationRuleDO.class;
    }

    @Override
    @Value("http_application_rule")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
