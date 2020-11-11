package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.base.AppDetect;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author : gxz
 * @date :  2019-12-17 11:11:59
 **/


@Repository
public class AppDetectDao extends SimpleBaseDaoImpl<AppDetect> {


    @Override
    protected Class<AppDetect> getClazz() {
        return AppDetect.class;
    }


    @Override
    @Value("app_detect")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


}
