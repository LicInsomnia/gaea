package com.tincery.gaea.core.base.dao.alarm;

import com.tincery.gaea.api.dm.alarm.statistic.ImpAlarmTargetStatistic;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ImpAlarmTargetStatisticDao extends SimpleBaseDaoImpl<ImpAlarmTargetStatistic> {

    @Override
    protected Class<ImpAlarmTargetStatistic> getClazz() {
        return ImpAlarmTargetStatistic.class;
    }

    @Override
    @Value("imp_alarm_target_statistic")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "proMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
