package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmUnitStatistic extends BaseStatistic implements MergeAble<AlarmUnitStatistic> {
    @Override
    public AlarmUnitStatistic merge(AlarmUnitStatistic alarmUnitStatistic) {
        return this;
    }
}
