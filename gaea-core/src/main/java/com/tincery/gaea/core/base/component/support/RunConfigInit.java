package com.tincery.gaea.core.base.component.support;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.config.RunConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class RunConfigInit implements ApplicationListener<ContextRefreshedEvent> {

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始加载运行配置");
        Query query = new Query(Criteria.where("_id").is(NodeInfo.getNodeName()));
        JSONObject runConfig = sysMongoTemplate.findOne(query, JSONObject.class, "run_config");
        if (runConfig != null) {
            JSONObject categoryJson = runConfig.getJSONObject(ApplicationInfo.getCategory());
            if (categoryJson == null) {
                log.warn("模块：{}无运行配置", ApplicationInfo.getCategory());
            } else {
                RunConfig.init(categoryJson);
            }
        }
    }
}
