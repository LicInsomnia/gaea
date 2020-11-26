package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmLevelStatistic extends BaseStatistic implements MergeAble<AlarmLevelStatistic> {
    @Override
    public AlarmLevelStatistic merge(AlarmLevelStatistic alarmLevelStatistic) {
        return this;
    }
}
