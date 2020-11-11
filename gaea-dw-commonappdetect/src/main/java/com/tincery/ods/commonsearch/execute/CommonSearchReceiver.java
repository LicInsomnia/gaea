package com.tincery.ods.commonsearch.execute;

import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.config.RunConfig;
import com.tincery.gaea.core.base.dao.AppDetectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CommonSearchReceiver implements Receiver {


    @Autowired
    private AppDetectDao appDetectDao;
    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;


    @Override
    public void receive(TextMessage textMessage) throws JMSException {



    }

    @Override
    public void init() {
        Date startTime = RunConfig.getDate("startTime");
        long duration = RunConfig.getLong("duration");

        List<AppDetect> all = appDetectDao.findAll();
        for (AppDetect appDetect : all) {

        }
    }

    public void overWirteTime(LocalDateTime endTime){
        Query query = new Query(Criteria.where("_id").is(NodeInfo.getNodeName()));
        Update update = new Update();
        update.set(ApplicationInfo.getCategory() +".startTime",endTime);
        this.sysMongoTemplate.updateFirst(query,update,"run_config");
    }
}
