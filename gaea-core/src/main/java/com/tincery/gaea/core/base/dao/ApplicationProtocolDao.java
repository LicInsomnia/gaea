package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class ApplicationProtocolDao extends SimpleBaseDaoImpl<ApplicationInformationBO> {


    @Override
    protected Class<ApplicationInformationBO> getClazz() {
        return ApplicationInformationBO.class;
    }

    @Override
    @Value("application_protocol")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name="sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
