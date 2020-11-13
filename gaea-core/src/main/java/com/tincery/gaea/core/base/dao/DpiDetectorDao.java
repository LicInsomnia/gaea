package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.DpiDetectorDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class DpiDetectorDao extends SimpleBaseDaoImpl<DpiDetectorDO> {

    @Override
    protected Class<DpiDetectorDO> getClazz() {
        return DpiDetectorDO.class;
    }

    @Override
    @Value("dpi_protocol")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}