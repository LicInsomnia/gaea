package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.dm.AssetDataDTO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author : gxz
 * @date :  2019-12-17 11:11:59
 **/


@Repository
public class AssetPortDao extends SimpleBaseDaoImpl<AssetDataDTO> {


    @Override
    protected Class<AssetDataDTO> getClazz() {
        return AssetDataDTO.class;
    }


    @Override
    @Value("asset_port_statistic")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


}
