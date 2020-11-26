package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.dm.AssetExtension;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AssetExtensionStatisticDao extends SimpleBaseDaoImpl<AssetExtension> {

    @Override
    protected Class<AssetExtension> getClazz() {
        return AssetExtension.class;
    }

    @Override
    @Value("asset_extension_statistic")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
