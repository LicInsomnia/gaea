package com.tincery.support.mongostash.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.TableConfigDao;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.starter.base.model.TableConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class MongoStashReceiver implements Receiver {


    @Resource(name = "proMongoTemplate")
    private MongoTemplate proMongoTemplate;

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Autowired
    private TableConfigDao tableConfigDao;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        long jmsTimestamp = textMessage.getJMSTimestamp();
        List<TableConfig> updateConfig = tableConfigDao.getUpdate();
        List<TableConfig> insertConfig = tableConfigDao.getInsert();
        update(updateConfig);
        insert(insertConfig);
    }

    private void update(List<TableConfig> updateConfig) {
        if (CollectionUtils.isEmpty(updateConfig)) {
            return;
        }
        for (TableConfig tableConfig : updateConfig) {
            List<String> updateElements = tableConfig.getUpdateElements();
            List<String> updateDate = tableConfig.getUpdateDate();
            String cacheByCategory = NodeInfo.getCacheByCategory(tableConfig.getId());
            File categoryCacheFile = new File(cacheByCategory);
            if (!categoryCacheFile.exists()) {
                continue;
            }
            File[] files = categoryCacheFile.listFiles();
            if (files != null) {
                Arrays.stream(files).map(FileUtils::readLine).flatMap(Collection::stream).map(JSON::parseObject).forEach(jsonObject -> {
                    Update update = new Update();
                    boolean updateFlag = false;
                    for (String updateElement : updateElements) {
                        if(jsonObject.containsKey(updateElement)){
                            updateFlag = true;
                            update.set(updateElement,jsonObject.get(updateElement));
                        }
                    }
                    for (String updateDateElement : updateDate) {
                         if(jsonObject.containsKey(updateDateElement)){
                             updateFlag = true;
                             Date date = jsonObject.getDate(updateDateElement);
                             update.set(updateDateElement,date);
                         }
                    }
                    if(updateFlag){
                        Query query = new Query(Criteria.where("_id").is(jsonObject.getString("_id")));
                        proMongoTemplate.updateFirst(query,update,tableConfig.getName());
                    }
                });
            }
        }
    }

    private void insert(List<TableConfig> insertConfig) {
        if (CollectionUtils.isEmpty(insertConfig)) {
            return;
        }
        for (TableConfig tableConfig : insertConfig) {
            String id = tableConfig.getId();
            String cacheByCategory = NodeInfo.getCacheByCategory(id);
            String name = tableConfig.getName();
            File categoryCacheFile = new File(cacheByCategory);
            if (!categoryCacheFile.exists()) {
                continue;
            }
            File[] files = categoryCacheFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    List<String> list = FileUtils.readLine(file);
                    List<JSONObject> insertData = list.stream().map(JSON::parseObject).collect(Collectors.toList());
                    if (tableConfig.getType().contains("production")) {
                        proMongoTemplate.insert(insertData, name);
                    } else {
                        sysMongoTemplate.insert(insertData, name);
                    }
                }
            }
        }
    }

    @Override
    public void init() {

    }

}
