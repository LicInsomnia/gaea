package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.dm.Alarm;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AlarmDao extends SimpleBaseDaoImpl<Alarm> {

    @Override
    protected Class<Alarm> getClazz() {
        return Alarm.class;
    }

    @Override
    @Value("alarm")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
