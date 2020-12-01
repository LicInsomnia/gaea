package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.KV;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.api.dm.alarm.statistic.*;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Insomnia
 */
public class AlarmStatisticSupport {


    public static AlarmStatistic convertAlarm2AlarmStatistic(Alarm alarm) {
        AlarmStatistic alarmStatistic = new AlarmStatistic();
        alarmStatistic.setCategoryDescription(alarm.getCategoryDesc())
                .setSubCategoryDescription(alarm.getSubCategoryDesc())
                .setTitle(alarm.getTitle())
                .setType(alarm.getType())
                .setCreator(alarm.getCreateUser())
                .setLevel(alarm.getLevel())
                .setCount(1)
                .setId();
        return alarmStatistic;
    }

    public static AlarmCategoryStatistic convertAlarm2AlarmCategoryStatistic(Alarm alarm) {
        AlarmCategoryStatistic alarmCategoryStatistic = new AlarmCategoryStatistic();
        alarmCategoryStatistic.setCategoryDescription(alarm.getCategoryDesc())
                .setSubCategoryDescription(alarm.getSubCategoryDesc())
                .setTotalCount(1)
                .setInsertTime(DateUtils.Long2LocalDateTime(alarm.getCapTime()))
                .setUpdateTime(DateUtils.Long2LocalDateTime(alarm.getCapTime() + alarm.getDuration()));
        KV<LocalDateTime, Long> timeCountStatistic = new KV<>(DateUtils.getDayTime(alarm.getCapTime()), 1L);
        List<KV<LocalDateTime, Long>> timeCountStatisticList = new ArrayList<>();
        timeCountStatisticList.add(timeCountStatistic);
        alarmCategoryStatistic.setTimeCountStatisticList(timeCountStatisticList).setId();
        return alarmCategoryStatistic;
    }

    public static AlarmCountryStatistic convertAlarm2AlarmCountryStatistic(Alarm alarm) {
        Location location = alarm.getServerLocation();
        if (null == location) {
            return null;
        }
        String country = location.getCountry();
        if (null == country) {
            return null;
        }
        AlarmCountryStatistic alarmCountryStatistic = new AlarmCountryStatistic();
        alarmCountryStatistic.setCountry(country).setCount(1L).setTimeTag(DateUtils.getDayTime(alarm.getCapTime())).setId();
        return alarmCountryStatistic;
    }

    public static AlarmCountStatistic convertAlarm2AlarmCountStatistic(Alarm alarm) {
        AlarmCountStatistic alarmCountStatistic = new AlarmCountStatistic();
        alarmCountStatistic.setTimeTag(DateUtils.getDayTime(alarm.getCapTime())).setCount(1L).setId();
        return alarmCountStatistic;
    }

    public static AlarmLevelStatistic convertAlarm2AlarmLevelStatistic(Alarm alarm) {
        AlarmLevelStatistic alarmLevelStatistic = new AlarmLevelStatistic();
        alarmLevelStatistic.setTimeTag(DateUtils.getDayTime(alarm.getCapTime()));
        List<KV<String, Long>> levelCountList = new ArrayList<>();
        levelCountList.add(new KV<>(alarm.getLevel() + "告警", 1L));
        if (alarm.getType().equals("threat")) {
            levelCountList.add(new KV<>("威胁情报告警", 1L));
        } else if (!alarm.getType().equals("leak")) {
            levelCountList.add(new KV<>("密数据分析告警", 1L));
        }
        if (alarm.getProName().equals(HeadConst.PRONAME.SSL)) {
            levelCountList.add(new KV<>("SSL告警", 1L));
        }
        alarmLevelStatistic.setLevelCountList(levelCountList).setId();
        return alarmLevelStatistic;
    }

    public static AlarmSslStatistic convertAlarm2AlarmSslStatistic(Alarm alarm) {
        AlarmSslStatistic alarmSslStatistic = new AlarmSslStatistic();
        if (!alarm.getProName().equals(HeadConst.PRONAME.SSL) || null == alarmSslStatistic.getSubCategoryDescription()) {
            return null;
        }
        alarmSslStatistic.setSubCategoryDescription(alarm.getSubCategoryDesc())
                .setCount(1L)
                .setId();
        return alarmSslStatistic;
    }

    public static AlarmUnitStatistic convertAlarm2AlarmUnitStatistic(Alarm alarm) {
        AlarmUnitStatistic alarmUnitStatistic = new AlarmUnitStatistic();
        if (null == alarm.getAssetIp()) {
            return null;
        }
        String assetIp = alarm.getAssetIp();
        String country;
        if (assetIp.equals(alarm.getClientIp())) {
            country = alarm.getClientLocation().getCountry();
        } else {
            country = alarm.getServerLocation().getCountry();
        }
        OppositeIp oppositeIp = new OppositeIp(assetIp, country, alarm.getLevel(), 1L);
        List<OppositeIp> oppositeIps = new ArrayList<>();
        oppositeIps.add(oppositeIp);
        alarmUnitStatistic.setUnit(alarm.getAssetUnit())
                .setLevel(alarm.getLevel())
                .setCount(1L)
                .setOppositeIps(oppositeIps)
                .setId();
        return alarmUnitStatistic;
    }

    public static AlarmUserStatistic convertAlarm2AlarmUserStatistic(Alarm alarm) {
        AlarmUserStatistic alarmUserStatistic = new AlarmUserStatistic();
        alarmUserStatistic.setCategoryDescription(alarm.getCategoryDesc())
                .setSubCategoryDescription(alarm.getSubCategoryDesc())
                .setTitle(alarm.getTitle())
                .setType(alarm.getType());
        Set<String> user = new HashSet<>();
        if (null == alarm.getTargetName()) {
            user.add(alarm.getUserId());
        } else {
            user.add(alarm.getTargetName());
        }
        alarmUserStatistic.setUser(user).setUserCount(user.size());
        Set<String> asset = new HashSet<>();
        String assetName = null;
        if (null != alarm.getAssetIp()) {
            assetName = alarm.getAssetUnit() + "[" + alarm.getAssetName() + "(" + alarm.getAssetIp() + ")]";
        }
        if (null != assetName) {
            asset.add(assetName);
        }
        alarmUserStatistic.setAssetCount(asset.size()).setId();
        return alarmUserStatistic;
    }

    public static ImpAlarmCategoryStatistic convertAlarm2ImpAlarmCategoryStatistic(Alarm alarm) {
        ImpAlarmCategoryStatistic impAlarmCategoryStatistic = new ImpAlarmCategoryStatistic();
        impAlarmCategoryStatistic.setCategoryDescription(alarm.getCategoryDesc())
                .setSubCategoryDescription(alarm.getSubCategoryDesc())
                .setTitle(alarm.getTitle())
                .setInsertTime(DateUtils.Long2LocalDateTime(alarm.getCapTime()))
                .setUpdateTime(DateUtils.Long2LocalDateTime(alarm.getCapTime()));
        TargetStatistic targetStatistic = new TargetStatistic(alarm);
        List<TargetStatistic> targets = new ArrayList<>();
        targets.add(targetStatistic);
        impAlarmCategoryStatistic.setTargets(targets).setId();
        return impAlarmCategoryStatistic;
    }

    public static ImpAlarmTargetStatistic convertAlarm2ImpAlarmTargetStatistic(Alarm alarm) {
        ImpAlarmTargetStatistic impAlarmTargetStatistic = new ImpAlarmTargetStatistic();
        String categoryDescription = alarm.getCategoryDesc();
        String subCategoryDescription = alarm.getSubCategoryDesc();
        String title = alarm.getIsSystem() ? "*" : alarm.getTitle();
        if (null == categoryDescription || null == subCategoryDescription || null == title) {
            return null;
        }
        String userId = alarm.getUserId();
        if (null == userId) {
            return null;
        }
        OppositeIp oppositeIp;
        if (null != alarm.getTargetName()) {
            impAlarmTargetStatistic.setKey(alarm.getTargetName());
            impAlarmTargetStatistic.setTargetType("targetName");
            oppositeIp = new OppositeIp(alarm.getServerIp(), alarm.getServerLocation().getCountry(),
                    DateUtils.Long2LocalDateTime(alarm.getCapTime()));
        } else if (null != alarm.getAssetIp()) {
            impAlarmTargetStatistic.setKey(alarm.getAssetIp());
            impAlarmTargetStatistic.setTargetType("asset");
            if (impAlarmTargetStatistic.getKey().equals(alarm.getClientIp())) {
                oppositeIp = new OppositeIp(alarm.getServerIp(), alarm.getServerLocation().getCountry(),
                        DateUtils.Long2LocalDateTime(alarm.getCapTime()));
            } else {
                oppositeIp = new OppositeIp(alarm.getClientIp(), alarm.getClientLocation().getCountry(),
                        DateUtils.Long2LocalDateTime(alarm.getCapTime()));
            }
        } else {
            impAlarmTargetStatistic.setKey(userId);
            if (ToolUtils.isIpv4(userId)) {
                impAlarmTargetStatistic.setTargetType("clientIp");
            } else {
                impAlarmTargetStatistic.setTargetType("userId");
            }
            oppositeIp = new OppositeIp(alarm.getServerIp(), alarm.getServerLocation().getCountry(),
                    DateUtils.Long2LocalDateTime(alarm.getCapTime()));
        }
        Set<String> userIds = new HashSet<>();
        userIds.add(userId);
        long capTime = alarm.getCapTime();
        TargetAlarm targetAlarm = new TargetAlarm(categoryDescription, subCategoryDescription, title,
                alarm.getLevel(), 1L, alarm.getIsSystem(), oppositeIp);
        List<TargetAlarm> alarmList = new ArrayList<>();
        alarmList.add(targetAlarm);
        impAlarmTargetStatistic.setInsertTime(DateUtils.Long2LocalDateTime(capTime))
                .setUpdateTime(DateUtils.Long2LocalDateTime(capTime))
                .setLevel(alarm.getLevel())
                .setCount(1L)
                .setUserIds(userIds)
                .setAlarmList(alarmList);
        impAlarmTargetStatistic.setId();
        return impAlarmTargetStatistic;
    }

    public static <T> List<KV<T, Long>> mergekv(List<KV<T, Long>> kv1, List<KV<T, Long>> kv2) {
        Map<T, KV<T, Long>> map = new HashMap<>();
        for (KV<T, Long> kv : kv1) {
            if (map.containsKey(kv.getKey())) {
                KV<T, Long> buffer = map.get(kv.getKey());
                kv.setValue(kv.getValue() + buffer.getValue());
            } else {
                map.put(kv.getKey(), kv);
            }
        }
        for (KV<T, Long> kv : kv2) {
            if (map.containsKey(kv.getKey())) {
                KV<T, Long> buffer = map.get(kv.getKey());
                kv.setValue(kv.getValue() + buffer.getValue());
            } else {
                map.put(kv.getKey(), kv);
            }
        }
        return new ArrayList<>(map.values());
    }

}

