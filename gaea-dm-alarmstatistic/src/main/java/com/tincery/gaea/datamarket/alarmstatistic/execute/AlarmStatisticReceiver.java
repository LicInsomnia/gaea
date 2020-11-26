package com.tincery.gaea.datamarket.alarmstatistic.execute;

import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.api.dm.alarm.statistic.AlarmStatistic;
import com.tincery.gaea.core.base.component.support.MergeSupport;
import com.tincery.gaea.core.base.dao.alarm.*;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sparrow
 */
@Slf4j
@Service
public class AlarmStatisticReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private AlarmStatisticDao alarmStatisticDao;
    @Autowired
    private AlarmCategoryStatisticDao alarmCategoryStatisticDao;
    @Autowired
    private AlarmCountryStatisticDao alarmCountryStatisticDao;
    @Autowired
    private AlarmCountStatisticDao alarmCountStatisticDao;
    @Autowired
    private AlarmLevelStatisticDao alarmLevelStatisticDao;
    @Autowired
    private AlarmSslStatisticDao alarmSslStatisticDao;
    @Autowired
    private AlarmUnitStatisticDao alarmUnitStatisticDao;
    @Autowired
    private AlarmUserStatisticDao alarmUserStatisticDao;
    @Autowired
    private ImpAlarmCategoryStatisticDao impAlarmCategoryStatisticDao;
    @Autowired
    private ImpAlarmTargetStatisticDao impAlarmTargetStatisticDao;

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
        List<Alarm> alarms = alarmDao.findListData(query);
        log.info("检测到最新告警{}条", alarms.size());
        alarmStatistic(alarms);
        System.out.println(1);
    }

    private void alarmStatistic(List<Alarm> alarms) {
        Map<String, List<AlarmStatistic>> collection = alarms.stream()
                .map(this::createMergeableByAlarm)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(MergeAble::getId));
        collection.forEach((id, list) -> {
            MergeSupport.rechecking(alarmStatisticDao, list);
        });
        log.info("{}合并插入{}条数据", alarmStatisticDao.getDbName(), collection.size());
    }

    public List<AlarmStatistic> createMergeableByAlarm(Alarm alarm) {
        List<AlarmStatistic> statisticList = new ArrayList<>();
        AlarmStatistic statistic = new AlarmStatistic(alarm);
        statisticList.add(statistic);
        return statisticList;
    }

    @Override
    protected void dmFileAnalysis(File file) {
    }

    @Override
    public void init() {

    }

}
