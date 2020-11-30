package com.tincery.gaea.core.base.dao.alarm;

import com.tincery.gaea.api.dm.alarm.statistic.AlarmUserStatistic;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AlarmUserStatisticDao extends SimpleBaseDaoImpl<AlarmUserStatistic> {

    @Override
    protected Class<AlarmUserStatistic> getClazz() {
        return AlarmUserStatistic.class;
    }

    @Override
    @Value("alarm_user_statistic")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
