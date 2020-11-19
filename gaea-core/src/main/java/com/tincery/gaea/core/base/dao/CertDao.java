package com.tincery.gaea.core.base.dao;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.CertDo;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import weka.gui.simplecli.Echo;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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


    public List<CertDo> getDataList(){
        List<CertDo> list = new ArrayList<>();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("compliance").is(-1));
            query.addCriteria(Criteria.where("reliability").is(-1));
            list.addAll(findListData(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }

    public Long updateData(String id, Document doc) {
        Update updateData = new Update();
        for(String key : doc.keySet()) {
            updateData.set(key, doc.get(key));
        }
        return update(id, updateData);
    }

}
