package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmCountStatistic extends BaseStatistic implements MergeAble<AlarmCountStatistic> {

    @Override
    public AlarmCountStatistic merge(AlarmCountStatistic alarmCountStatistic) {
        return null;
    }
}
