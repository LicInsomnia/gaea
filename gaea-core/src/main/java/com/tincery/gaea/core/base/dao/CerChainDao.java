package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.CerChainDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;

@Repository
public class CerChainDao extends SimpleBaseDaoImpl<CerChainDO> {
    @Override
    protected Class<CerChainDO> getClazz() {
        return CerChainDO.class;
    }

    @Override
    @Value("cerchain")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Long updateData(String id, Document doc) {
        Update updateData = new Update();
        for(String key : doc.keySet()) {
            updateData.set(key, doc.get(key));
        }
        return update(id, updateData);
    }
}
