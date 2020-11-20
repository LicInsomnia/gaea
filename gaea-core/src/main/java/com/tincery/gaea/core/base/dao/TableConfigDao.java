package com.tincery.gaea.core.base.dao;


import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import com.tincery.starter.base.model.TableConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class TableConfigDao extends SimpleBaseDaoImpl<TableConfig> {


    @Override
    protected Class<TableConfig> getClazz() {
        return TableConfig.class;
    }

    @Override
    @Value("table_config")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<TableConfig> getUpdate() {
        Query query = new Query(Criteria.where("type").is("production_update"));
        return findListData(query);
    }

    public List<TableConfig> getInsert(){
        Query query = new Query(Criteria.where("type").regex("insert"));
        return findListData(query);
    }


}
