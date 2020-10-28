package com.tincery.gaea.core.base.dao;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.CertDo;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class CertDao extends SimpleBaseDaoImpl<CertDo> {


    @Override
    protected Class<CertDo> getClazz() {
        return CertDo.class;
    }

    @Override
    @Value("x509cert")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public JSONObject findOneById(String id, String... projections) {
        Query query = new Query(Criteria.where("_id").is(id));
        Field fields = query.fields();
        for (String projection : projections) {
            fields.include(projection);
        }
        fields.exclude("_id");
        return this.mongoTemplate.findOne(query, JSONObject.class, this.getDbName());
    }

    public JSONObject findOneById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, JSONObject.class, this.getDbName());
    }


}
