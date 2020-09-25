package com.tincery.gaea.core.base.component.support;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class CommonConfigInit implements ApplicationListener<ContextRefreshedEvent> {

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始加载通用配置");
        Query query = new Query(Criteria.where("_id").is(NodeInfo.getNodeName()));
        List<JSONObject> commonConfigs = sysMongoTemplate.findAll(JSONObject.class,"common_config");
        JSONObject runConfig = sysMongoTemplate.findOne(query,JSONObject.class,"run_config");
        commonConfigs.forEach((commonConfig) -> CommonConfig.put(commonConfig.getString("_id"), commonConfig.get("value")));
        if(runConfig!=null){
            runConfig.forEach(CommonConfig::mergeCommonRun);
        }
        CommonConfig.validatorCommonConfig();
    }
}
