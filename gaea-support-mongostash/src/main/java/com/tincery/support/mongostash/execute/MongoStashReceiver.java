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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
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
        List<TableConfig> update = tableConfigDao.getUpdate();
        List<TableConfig> insert = tableConfigDao.getInsert();
        update(update);
        insert(insert);
    }

    private void update(List<TableConfig> updateConfig) {
        if (CollectionUtils.isEmpty(updateConfig)) {
            return;
        }

        for (TableConfig tableConfig : updateConfig) {
//            List<String> updateElements = tableConfig.getUpdateElements();
//            List<String> updateDate = tableConfig.getUpdateDate();

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
