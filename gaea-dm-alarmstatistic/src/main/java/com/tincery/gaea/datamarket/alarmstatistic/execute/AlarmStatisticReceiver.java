package com.tincery.gaea.datamarket.alarmstatistic.execute;

import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.api.dm.alarm.statistic.*;
import com.tincery.gaea.core.base.dao.alarm.AlarmDao;
import com.tincery.gaea.core.base.dao.alarm.AlarmStatisticDao;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import com.tincery.gaea.core.dw.MergeAble;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.tincery.gaea.core.base.component.support.AlarmStatisticSupport.*;

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
    private ApplicationContext applicationContext;

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
        Map<String, List<MergeAble>> collect = alarms.stream()
                .map(this::createMergeableByAlarm)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(MergeAble::getId));
        Map<String, MergeAble> mergedMap = new HashMap<>();
        collect.forEach((id, list) -> list.forEach(mergeAble -> mergedMap.merge(id, mergeAble, (k, v) -> v.merge(mergeAble))));
        final Map<String, List<MergeAble>> allStatistic = mergedMap.values().stream().collect(Collectors.groupingBy((x) -> x.getId().split("\\.")[0]));
        for (Map.Entry<String, List<MergeAble>> entry : allStatistic.entrySet()) {
            String clazz = entry.getKey();
            List<MergeAble> mergeAbles = entry.getValue();
            SimpleBaseDaoImpl dao = (SimpleBaseDaoImpl) this.applicationContext.getBean(clazz);
//            MergeSupport.rechecking(dao, mergeAbles);
        }
        log.info("{}合并插入{}条数据", alarmStatisticDao.getDbName(), collect.size());
    }

    public List<MergeAble<?>> createMergeableByAlarm(Alarm alarm) {
        List<MergeAble<?>> statisticList = new ArrayList<>();
        AlarmStatistic alarmStatistic = convertAlarm2AlarmStatistic(alarm);
        statisticList.add(alarmStatistic);
        AlarmCategoryStatistic alarmCategoryStatistic = convertAlarm2AlarmCategoryStatistic(alarm);
        statisticList.add(alarmCategoryStatistic);
        AlarmCountryStatistic alarmCountryStatistic = convertAlarm2AlarmCountryStatistic(alarm);
        statisticList.add(alarmCountryStatistic);
        AlarmCountStatistic alarmCountStatistic = convertAlarm2AlarmCountStatistic(alarm);
        statisticList.add(alarmCountStatistic);
        AlarmLevelStatistic alarmLevelStatistic = convertAlarm2AlarmLevelStatistic(alarm);
        statisticList.add(alarmLevelStatistic);
        AlarmSslStatistic alarmSslStatistic = convertAlarm2AlarmSslStatistic(alarm);
        statisticList.add(alarmSslStatistic);
        AlarmUserStatistic alarmUserStatistic = convertAlarm2AlarmUserStatistic(alarm);
        statisticList.add(alarmUserStatistic);
//        AlarmUnitStatistic alarmUnitStatistic = convertAlarm2AlarmUnitStatistic(alarm);
//        statisticList.add(alarmUnitStatistic);
        ImpAlarmCategoryStatistic impAlarmCategoryStatistic = convertAlarm2ImpAlarmCategoryStatistic(alarm);
        statisticList.add(impAlarmCategoryStatistic);
//        ImpAlarmTargetStatistic impAlarmTargetStatistic = convertAlarm2ImpAlarmTargetStatistic(alarm);
//        statisticList.add(impAlarmTargetStatistic);
        return statisticList;
    }

    @Override
    protected void dmFileAnalysis(File file) {
    }

    @Override
    public void init() {

    }

}
