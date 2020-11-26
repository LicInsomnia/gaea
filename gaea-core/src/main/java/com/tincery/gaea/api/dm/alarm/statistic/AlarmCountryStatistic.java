package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmCountryStatistic extends BaseStatistic implements MergeAble<AlarmCountryStatistic> {
    @Override
    public AlarmCountryStatistic merge(AlarmCountryStatistic alarmCountryStatistic) {
        return this;
    }
}
