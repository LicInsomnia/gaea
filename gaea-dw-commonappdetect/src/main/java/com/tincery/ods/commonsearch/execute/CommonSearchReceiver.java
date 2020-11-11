package com.tincery.ods.commonsearch.execute;

import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.RunConfig;
import com.tincery.gaea.core.base.dao.AppDetectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
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
        LocalDateTime startTime = RunConfig.getLocalDateTime("startTime");
        long duration = RunConfig.getLong("duration");

        List<AppDetect> all = appDetectDao.findAll();
        for (AppDetect appDetect : all) {

        }
    }

}
