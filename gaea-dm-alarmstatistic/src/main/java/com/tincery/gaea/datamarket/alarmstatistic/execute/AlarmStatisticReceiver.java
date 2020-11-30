package com.tincery.gaea.datamarket.alarmstatistic.execute;

import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.api.dm.alarm.statistic.*;
import com.tincery.gaea.core.base.component.support.MergeSupport;
import com.tincery.gaea.core.base.dao.alarm.*;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
        Map<String, List<MergeAble>> statisticMap = alarmStatistic(alarms);
        updateStatistic(statisticMap);
        Update update = new Update().set("handle", true);
        this.alarmDao.update(query, update);
    }

    private Map<String, List<MergeAble>> alarmStatistic(List<Alarm> alarms) {
        Map<String, List<MergeAble>> collect = alarms.stream()
                .map(this::createMergeableByAlarm)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(MergeAble::getId));
        Map<String, MergeAble> mergedMap = new HashMap<>();
        collect.forEach((id, list) -> list.forEach(mergeAble -> mergedMap.merge(id, mergeAble, (k, v) -> (MergeAble) v.merge(mergeAble))));
        return mergedMap.values().stream().collect(Collectors.groupingBy((x) -> x.getId().split("\\.")[0]));
    }

    private List<MergeAble<?>> createMergeableByAlarm(Alarm alarm) {
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

    private void updateStatistic(Map<String, List<MergeAble>> statisticMap) {
        for (Map.Entry<String, List<MergeAble>> entry : statisticMap.entrySet()) {
            String key = entry.getKey();
            int count = 0;
            switch (key) {
                case "AlarmStatistic":
                    List<AlarmStatistic> alarmStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmStatistic alarmStatistic = (AlarmStatistic) mergeAble;
                        alarmStatistics.add(alarmStatistic);
                        count++;
                    }
                    MergeSupport.rechecking(this.alarmStatisticDao, alarmStatistics);
                    log.info("{}合并插入{}条数据", alarmStatisticDao.getDbName(), count);
                    break;
                case "AlarmCategoryStatistic":
                    List<AlarmCategoryStatistic> alarmCategoryStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmCategoryStatistic alarmCategoryStatistic = (AlarmCategoryStatistic) mergeAble;
                        alarmCategoryStatistics.add(alarmCategoryStatistic);
                        count++;
                    }
                    MergeSupport.rechecking(this.alarmCategoryStatisticDao, alarmCategoryStatistics);
                    log.info("{}合并插入{}条数据", alarmCategoryStatisticDao.getDbName(), count);
                    break;
                case "AlarmCountryStatistic":
                    List<AlarmCountryStatistic> alarmCountryStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmCountryStatistic alarmCountryStatistic = (AlarmCountryStatistic) mergeAble;
                        alarmCountryStatistics.add(alarmCountryStatistic);
                        count++;
                    }
                    MergeSupport.rechecking(this.alarmCountryStatisticDao, alarmCountryStatistics);
                    log.info("{}合并插入{}条数据", alarmCountryStatisticDao.getDbName(), count);
                    break;
                case "AlarmCountStatistic":
                    List<AlarmCountStatistic> alarmCountStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmCountStatistic alarmCountStatistic = (AlarmCountStatistic) mergeAble;
                        alarmCountStatistics.add(alarmCountStatistic);
                    }
                    MergeSupport.rechecking(this.alarmCountStatisticDao, alarmCountStatistics);
                    log.info("{}合并插入{}条数据", alarmCountStatisticDao.getDbName(), count);
                    break;
                case "AlarmLevelStatistic":
                    List<AlarmLevelStatistic> alarmLevelStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmLevelStatistic alarmLevelStatistic = (AlarmLevelStatistic) mergeAble;
                        alarmLevelStatistics.add(alarmLevelStatistic);
                    }
                    MergeSupport.rechecking(this.alarmLevelStatisticDao, alarmLevelStatistics);
                    log.info("{}合并插入{}条数据", alarmLevelStatisticDao.getDbName(), count);
                    break;
                case "AlarmSslStatistic":
                    List<AlarmSslStatistic> alarmSslStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmSslStatistic alarmSslStatistic = (AlarmSslStatistic) mergeAble;
                        alarmSslStatistics.add(alarmSslStatistic);
                    }
                    MergeSupport.rechecking(this.alarmSslStatisticDao, alarmSslStatistics);
                    log.info("{}合并插入{}条数据", alarmSslStatisticDao.getDbName(), count);

                    break;
                case "AlarmUnitStatistic":
                    List<AlarmUnitStatistic> alarmUnitStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmUnitStatistic alarmUnitStatistic = (AlarmUnitStatistic) mergeAble;
                        alarmUnitStatistics.add(alarmUnitStatistic);
                    }
                    MergeSupport.rechecking(this.alarmUnitStatisticDao, alarmUnitStatistics);
                    log.info("{}合并插入{}条数据", alarmUnitStatisticDao.getDbName(), count);
                    break;
                case "AlarmUserStatistic":
                    List<AlarmUserStatistic> alarmUserStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        AlarmUserStatistic alarmUserStatistic = (AlarmUserStatistic) mergeAble;
                        alarmUserStatistics.add(alarmUserStatistic);
                    }
                    MergeSupport.rechecking(this.alarmUserStatisticDao, alarmUserStatistics);
                    log.info("{}合并插入{}条数据", alarmUserStatisticDao.getDbName(), count);
                    break;
                case "ImpAlarmCategoryStatistic":
                    List<ImpAlarmCategoryStatistic> impAlarmCategoryStatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        ImpAlarmCategoryStatistic impAlarmCategoryStatistic = (ImpAlarmCategoryStatistic) mergeAble;
                        impAlarmCategoryStatistics.add(impAlarmCategoryStatistic);
                    }
                    MergeSupport.rechecking(this.impAlarmCategoryStatisticDao, impAlarmCategoryStatistics);
                    log.info("{}合并插入{}条数据", impAlarmCategoryStatisticDao.getDbName(), count);
                    break;
                case "ImpAlarmTargetStatistic":
                    List<ImpAlarmTargetStatistic> impAlarmTargettatistics = new ArrayList<>();
                    for (MergeAble mergeAble : entry.getValue()) {
                        ImpAlarmTargetStatistic impAlarmTargetStatistic = (ImpAlarmTargetStatistic) mergeAble;
                        impAlarmTargettatistics.add(impAlarmTargetStatistic);
                    }
                    MergeSupport.rechecking(this.impAlarmTargetStatisticDao, impAlarmTargettatistics);
                    log.info("{}合并插入{}条数据", impAlarmTargetStatisticDao.getDbName(), count);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void dmFileAnalysis(File file) {
    }

    @Override
    public void init() {

    }

}
