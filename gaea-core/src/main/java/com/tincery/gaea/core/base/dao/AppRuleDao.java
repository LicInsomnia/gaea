package com.tincery.gaea.core.base.dao;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AppRule;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author : gxz
 * @date :  2019-12-17 11:11:59
 **/


@Repository
public class AppRuleDao extends SimpleBaseDaoImpl<AppRule> {

    private static final int MULTI_THREAD_QUERY = 10000;


    @Override
    protected Class<AppRule> getClazz() {
        return AppRule.class;
    }


    @Override
    @Value("apprule")
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    @Resource(name = "sysMongoTemplate")
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<JSONObject> findAllJSON() {
        int listCount = Integer.parseInt(this.findListCount(new Query()).toString());
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<List<JSONObject>> task = new ForkJoinGetRule(0, listCount);
        return pool.invoke(task);
    }

    private synchronized List<JSONObject> distinctAndJoin(List<JSONObject> a, List<JSONObject> b) {
        a.addAll(b);
        return a;
    }

    /****
     * 多线程分段查询映射  解决规则太多 映射不过来的情况
     * @author gxz
     **/
    private class ForkJoinGetRule extends RecursiveTask<List<JSONObject>> {
        private final int begin; //查询开始位置
        private final int end;

        ForkJoinGetRule(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        @Override
        protected List<JSONObject> compute() {
            int count = end - begin;
            if (MULTI_THREAD_QUERY >= count) {
                Query query = new Query();
                query.skip(begin).limit(count);
                return AppRuleDao.this.mongoTemplate.find(query, JSONObject.class, AppRuleDao.this.getDbName());
            } else {
                int middle = (begin + end) / 2;
                ForkJoinGetRule pre = new ForkJoinGetRule(begin, middle);
                pre.fork();
                ForkJoinGetRule next = new ForkJoinGetRule(middle, end);
                next.fork();
                return distinctAndJoin(pre.join(), next.join());
            }
        }
    }


}
