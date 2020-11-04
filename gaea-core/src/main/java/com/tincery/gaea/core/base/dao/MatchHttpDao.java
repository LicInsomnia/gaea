package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.base.MatchHttpConfig;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


/**
 * @author gongxuanzhang
 */
@Repository
public class MatchHttpDao extends SimpleBaseDaoImpl<MatchHttpConfig> {


    @Override
    protected Class<MatchHttpConfig> getClazz() {
        return MatchHttpConfig.class;
    }


    @Override
    @Value("match_http_config")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


}
