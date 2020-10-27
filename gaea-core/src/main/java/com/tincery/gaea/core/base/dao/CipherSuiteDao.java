package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class CipherSuiteDao extends SimpleBaseDaoImpl<CipherSuiteDO> {


    @Override
    protected Class<CipherSuiteDO> getClazz() {
        return CipherSuiteDO.class;
    }

    @Override
    @Value("cipher_suite_config")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
