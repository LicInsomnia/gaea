package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.dm.AssetConfigDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class AssetConfigDao extends SimpleBaseDaoImpl<AssetConfigDO> {


    @Override
    protected Class<AssetConfigDO> getClazz() {
        return AssetConfigDO.class;
    }

    @Override
    @Value("asset_config")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<AssetConfigDO> getActivityData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("activity").is(true));
        return findListData(query);
    }

    public long updateStrartime() {
        Update update = new Update();
        update.set("starttime", LocalDateTime.now());
        return update(new Query(), update);
    }


}
