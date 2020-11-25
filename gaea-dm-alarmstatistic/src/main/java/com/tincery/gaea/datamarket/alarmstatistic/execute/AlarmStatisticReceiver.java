package com.tincery.gaea.datamarket.alarmstatistic.execute;

import com.tincery.gaea.api.dm.Alarm;
import com.tincery.gaea.core.base.dao.AlarmDao;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

/**
 * @author Sparrow
 */
@Slf4j
@Service
public class AlarmStatisticReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private AlarmDao alarmDao;

    @Override
    @Autowired
    protected void setDmProperties(DmProperties dmProperties) {
        this.dmProperties = dmProperties;
    }

    /**
     * @param textMessage 接收到的消息  存放要扫描的目录名
     */
    @Override
    public void receive(TextMessage textMessage) {
        log.info("alarmStatistic接收到了消息开始处理");
        Query query = new Query(Criteria.where("handle").is(false));
        List<Alarm> list = alarmDao.findListData(query);
        log.info("检测到最新告警{}条", list.size());
        System.out.println(1);
    }

    @Override
    protected void dmFileAnalysis(File file) {

    }

    @Override
    public void init() {

    }

}
