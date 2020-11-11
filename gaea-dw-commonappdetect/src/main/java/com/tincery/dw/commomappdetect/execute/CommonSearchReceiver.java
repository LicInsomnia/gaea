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
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CommonSearchReceiver extends AbstractDataWarehouseReceiver {


    @Autowired
    private AppDetectDao appDetectDao;

    @Override
    public void init() {
        List<AppDetect> all = appDetectDao.findAll();
        for (AppDetect appDetect : all) {

        }
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
