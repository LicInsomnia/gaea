package com.tincery.gaea.core.base.dao;


import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.core.base.mgt.SrcDictionary;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : gxz
 * @date :  2019-12-13 11:22:55
 **/


@Repository
public class SrcRuleDao extends SimpleBaseDaoImpl<SrcRuleDO> {


    private static final String FUNCTION = "function";

    private static final String GAEA_FLAG = "gaea_flag";

    @Autowired
    private SrcDictionary srcDictionary;

    @Override
    protected Class<SrcRuleDO> getClazz() {
        return SrcRuleDO.class;
    }

    @Override
    @Value("src_rule")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<SrcRuleDO> getPassData(String category) {
        final String pass = "pass";
        Query query = new Query(createCategoryRuleCriteria(category, pass));
        return findListData(query);
    }

    public List<SrcRuleDO> getLogData(String category) {
        final String log = "log";
        Query query = new Query(createCategoryRuleCriteria(category, log));
        return findListData(query);
    }

    public List<SrcRuleDO> getMarkData(String category) {
        final String mark = "mark";
        Query query = new Query(createCategoryRuleCriteria(category, mark));
        return findListData(query);
    }


    public List<SrcRuleDO> getAlarmData(String category){
        final String alarm = "alert";
        Query query = new Query(createCategoryRuleCriteria(category,alarm));
        return findListData(query);
    }

    public Criteria createCategoryRuleCriteria(String category, String type) {
        int functionValue = srcDictionary.valueOf(FUNCTION, type);
        int gaeaFlagValue = srcDictionary.valueOf("gaea_flag", category);
        return Criteria.where(FUNCTION).is(functionValue)
                .and(GAEA_FLAG).is(gaeaFlagValue)
                .and("activity").is(true);
    }

}
