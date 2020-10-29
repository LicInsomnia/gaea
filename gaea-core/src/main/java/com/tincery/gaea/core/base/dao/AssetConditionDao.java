package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.dm.AssetCondition;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : gxz
 * @date :  2019-12-17 11:11:59
 **/


@Repository
public class AssetConditionDao extends SimpleBaseDaoImpl<AssetCondition> {


    @Override
    protected Class<AssetCondition> getClazz() {
        return AssetCondition.class;
    }


    @Override
    @Value("asset_condition")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<AssetCondition> findActivityData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("activity").is(true));
        return findListData(query);
    }

}
