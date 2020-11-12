package com.tincery.dw.commomappdetect.execute;

import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.core.base.dao.AppDetectDao;
import com.tincery.gaea.core.dw.AbstractDataWarehouseReceiver;
import com.tincery.gaea.core.dw.DwProperties;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CommonSearchReceiver extends AbstractDataWarehouseReceiver {


    @Autowired
    private AppDetectDao appDetectDao;

    private Set<String> categorySet;

    @Override
    public void init() {
        List<AppDetect> all = appDetectDao.findAll();
        // 根据总步骤分组
        Map<Integer, List<AppDetect>> collect = all.stream().collect(Collectors.groupingBy(AppDetect::getRuleCount));
        // 根据category和步骤分组

        int index = 1;
        Map<String,List<String>> map = new HashMap<>();



        Set<String> categorySet = new HashSet<>();
        for (AppDetect appDetect : all) {
            categorySet.addAll(appDetect.getCategorySet());

        }
        this.categorySet = categorySet;
    }

    @Override
    @Autowired
    public void setProperties(DwProperties dwProperties) {
        this.dwProperties = dwProperties;
    }

    @Override
    @Resource(name="sysMongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void dataWarehouseAnalysis(LocalDateTime startTime, LocalDateTime endTime) {

    }

    @Override
    public List<Pair<String, String>> getCsvDataSet(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }


}
