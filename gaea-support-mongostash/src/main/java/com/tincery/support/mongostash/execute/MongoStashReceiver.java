package com.tincery.support.mongostash.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.TableConfigDao;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.starter.base.model.TableConfig;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
@Slf4j
public class MongoStashReceiver implements Receiver {


    @Resource(name = "proMongoTemplate")
    private MongoTemplate proMongoTemplate;

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    @Autowired
    private TableConfigDao tableConfigDao;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        log.info("开始执行");
        long jmsTimestamp = textMessage.getJMSTimestamp();
        List<TableConfig> updateConfig = tableConfigDao.getUpdate();
        List<TableConfig> insertConfig = tableConfigDao.getInsert();
        insertConfig.forEach(item->{
            log.info(item.getName());
        });
        update(updateConfig);
        insert(insertConfig);
        log.info("执行完成");
    }

    private void update(List<TableConfig> updateConfig) {
        if (CollectionUtils.isEmpty(updateConfig)) {
            return;
        }
        Map<String, Integer> updateCount = new HashMap<>();
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
                        if (jsonObject.containsKey(updateElement)) {
                            updateFlag = true;
                            update.set(updateElement, jsonObject.get(updateElement));
                        }
                    }
                    for (String updateDateElement : updateDate) {
                        if (jsonObject.containsKey(updateDateElement)) {
                            updateFlag = true;
                            Date date = jsonObject.getDate(updateDateElement);
                            update.set(updateDateElement, date);
                        }
                    }
                    if (updateFlag) {
                        Query query = new Query(Criteria.where("_id").is(jsonObject.getString("_id")));
                        proMongoTemplate.updateFirst(query, update, tableConfig.getName());
                        updateCount.merge(tableConfig.getName(), 1, Integer::sum);
                    }
                });
            }
        }
        updateCount.forEach((tableName, count) -> log.info("数据库{},修改了{}条记录", tableName, count));
    }

    private void insert(List<TableConfig> insertConfig) {
        if (CollectionUtils.isEmpty(insertConfig)) {
            return;
        }
        Map<String, Integer> insertCount = new HashMap<>();
        for (TableConfig tableConfig : insertConfig) {
            String id = tableConfig.getId();
            String cacheByCategory = NodeInfo.getCacheByCategory(id);
            String name = tableConfig.getName();
            File categoryCacheFile = new File(cacheByCategory);
            log.info("开始检索文件夹{}",categoryCacheFile.getName());
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
                    insertCount.merge(tableConfig.getName(), 1, Integer::sum);
                }
            }
        }
        insertCount.forEach((collectionName, count) -> log.info("数据库{}添加了{}条", collectionName, count));
    }

    @Override
    public void init() {

    }

}
