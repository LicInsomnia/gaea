package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Insomnia
 */
@Data
public class ImpAlarmTargetStatistic extends BaseStatistic implements MergeAble<ImpAlarmTargetStatistic> {

    private String key;
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

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.key;
    }

}
