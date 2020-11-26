package com.tincery.gaea.core.base.dao.alarm;

import com.tincery.gaea.api.dm.alarm.statistic.ImpAlarmCategoryStatistic;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ImpAlarmCategoryStatisticDao extends SimpleBaseDaoImpl<ImpAlarmCategoryStatistic> {

    @Override
    protected Class<ImpAlarmCategoryStatistic> getClazz() {
        return ImpAlarmCategoryStatistic.class;
    }

    @Override
    @Value("imp_alarm_category_statistic")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
