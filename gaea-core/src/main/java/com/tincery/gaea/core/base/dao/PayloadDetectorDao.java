package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.PayloadDetectorDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class PayloadDetectorDao extends SimpleBaseDaoImpl<PayloadDetectorDO> {


    @Override
    protected Class<PayloadDetectorDO> getClazz() {
        return PayloadDetectorDO.class;
    }

    @Override
    @Value("payload_detector")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
