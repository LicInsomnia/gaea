package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmUnitStatistic extends BaseStatistic implements MergeAble<AlarmUnitStatistic> {

    private double value = 0.0;
    private int level = 4;
    private long alarmCount;

    @Override
    public AlarmUnitStatistic merge(AlarmUnitStatistic alarmUnitStatistic) {
        return this;
    }

    @Override
    public void setId() {

    }
}
