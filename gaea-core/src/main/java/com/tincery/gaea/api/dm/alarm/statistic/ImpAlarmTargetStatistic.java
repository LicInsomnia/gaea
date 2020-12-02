package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Insomnia
 */
@Data
public class ImpAlarmTargetStatistic extends BaseStatistic implements MergeAble<ImpAlarmTargetStatistic> {

    private static List<String> levelIndex = Arrays.asList("紧急", "严重", "一般", "提醒");

    private String key;
    private String unit;
    private Long count;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
    private String level;
    private String targetType;
    private Set<String> userIds;
    private List<TargetAlarm> alarmList;
    private Double value;

    @Override
    public ImpAlarmTargetStatistic merge(ImpAlarmTargetStatistic that) {
        this.count += that.count;
        this.insertTime = DateUtils.min(this.insertTime, that.insertTime);
        this.updateTime = DateUtils.max(this.updateTime, that.updateTime);
        this.userIds.addAll(that.userIds);
        Map<String, TargetAlarm> targetAlarmMap = new HashMap<>();
        this.alarmList.forEach(targetAlarm -> targetAlarmMap.merge(targetAlarm.getId(), targetAlarm, (k, v) -> v.merge(targetAlarm)));
        that.alarmList.forEach(targetAlarm -> targetAlarmMap.merge(targetAlarm.getId(), targetAlarm, (k, v) -> v.merge(targetAlarm)));
        this.alarmList = new ArrayList<>(targetAlarmMap.values());
        return this;
    }

    public void adjustValue() {
        double sizeValue = Math.log(this.alarmList.size() + 1);
        double countValue = 0.0;
        for (TargetAlarm targetAlarm : this.alarmList) {
            int level = levelIndex.indexOf(targetAlarm.getLevel());
            long count = 10 * (targetAlarm.getCount() / 10 + 1);
            countValue += Math.pow(Math.log(count), Math.pow(5 - level, 3));
        }
        double value = Math.log(countValue) * sizeValue;
        BigDecimal bg = new BigDecimal(value);
        value = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        this.value = value;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.key;
    }

}
