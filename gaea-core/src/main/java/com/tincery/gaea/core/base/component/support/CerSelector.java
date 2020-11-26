package com.tincery.gaea.core.base.component.support;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.dao.CertDao;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础初始化类
 *
 * @author Insomnia
 * @date 2018/12/29
 */
@Component
public class CerSelector implements InitializationRequired {

    private final Map<String, JSONObject> cache = new ConcurrentHashMap<>();

    @Autowired
    private CommonConfig commonConfig;


    @Autowired
    private CertDao certDao;
    private String[] cerKeys;

    public JSONObject selector(String sha1) {
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
        if (commonConfig.getCerKeys() != null) {
            this.cerKeys = commonConfig.getCerKeys().toArray(new String[0]);
        } else {
            this.cerKeys = new String[0];
        }
    }
}
