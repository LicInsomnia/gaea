package com.tincery.gaea.core.base.mgt;

import com.tincery.gaea.api.base.ConstantComparisonTableDO;
import com.tincery.gaea.core.base.dao.ConstantComparisonTableDao;
import com.tincery.starter.base.Dictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: gxz gongxuanzhang@foxmail.com
 **/
@Component
@Slf4j
public class SrcDictionary implements Dictionary {


    private final ConstantComparisonTableDao constantComparisonTableDao;

    private static final Map<String, List<String>> DICTIONARY = new HashMap<>();

    @Autowired
    public SrcDictionary(ConstantComparisonTableDao constantComparisonTableDao) {
        this.constantComparisonTableDao = constantComparisonTableDao;
    }


    @Override
    public void init() {
        log.info("开始加载srcRule 码表");
        final String srcKey = "srcRule";
        ConstantComparisonTableDO constants = constantComparisonTableDao.findOne(new Query(Criteria.where("_id").is(srcKey)));
        constants.getContrast().forEach(contrast -> DICTIONARY.put(contrast.getKey(), contrast.getCode()));
        log.info("srcRule 加载完成");
    }

    @Override
    public String parse(String filed, int value) {
        List<String> values = DICTIONARY.get(filed);
        return values.get(value);
    }

    @Override
    public int valueOf(String filed, String value) {
        List<String> values = DICTIONARY.get(filed);
        return values.indexOf(value);
    }

}
