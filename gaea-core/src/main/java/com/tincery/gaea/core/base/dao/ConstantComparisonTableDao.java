package com.tincery.gaea.core.base.dao;

import com.tincery.gaea.api.base.ConstantComparisonTableDO;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Repository
public class ConstantComparisonTableDao extends SimpleBaseDaoImpl<ConstantComparisonTableDO> {


    @Override
    protected Class<ConstantComparisonTableDO> getClazz() {
        return ConstantComparisonTableDO.class;
    }

    @Override
    @Value("constant_comparison_table")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


}
