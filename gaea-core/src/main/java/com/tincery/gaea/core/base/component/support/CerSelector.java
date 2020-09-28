package com.tincery.gaea.core.base.component.support;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.CertDao;
import com.tincery.starter.base.InitializationRequired;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 基础初始化类
 *
 * @author Insomnia
 * @date 2018/12/29
 */
@Component
public class CerSelector implements InitializationRequired {

    @Autowired
    private CertDao certDao;

    private Map<String, JSONObject> cache = new HashMap<>();
    private String[] cerKeys;

    public Map<String, Object> selector(String sha1) {
        if (cache.containsKey(sha1)) {
            return this.cache.get(sha1);
        } else {
            JSONObject result = certDao.findOneById(sha1, this.cerKeys);
            if (null == result) {
                return null;
            }
            result.put("sha1", sha1);
            this.cache.put(sha1, result);
            return result;
        }
    }

    @Override
    public void init() {
        Object object = CommonConfig.get(NodeInfo.getCategory());
        if (null == object) {
            return;
        }
        Map<String, Object> configs = (Map<String, Object>) object;
        Set<String> cerKeys = new HashSet<>((List<String>) configs.get("cerkeys"));
        this.cerKeys = cerKeys.toArray(new String[0]);
    }
}
