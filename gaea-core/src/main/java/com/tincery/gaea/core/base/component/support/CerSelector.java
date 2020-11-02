package com.tincery.gaea.core.base.component.support;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.dao.CertDao;
import com.tincery.starter.base.InitializationRequired;
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

    private final Map<String, JSONObject> cache = new HashMap<>();
    @Autowired
    private CertDao certDao;
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
    @SuppressWarnings("unchecked")
    public void init() {
        Map<String, Object> configs = (Map<String, Object>) CommonConfig.get("cerKeys");
        if(configs == null){
            this.cerKeys = new String[0];
            return;
        }
        /*List<Map<String, Object>> fields = (List<Map<String, Object>>) configs.get("fields");
        Set<String> cerKeys = new HashSet<>();
        for (Map<String, Object> field : fields) {
            cerKeys.add(field.get("key").toString());
        }*/

        List<String> fields = (List<String>) configs.get("fields");
        Set<String> cerKeys = new HashSet<>(fields);
        this.cerKeys = cerKeys.toArray(new String[0]);
    }
}
