package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmSslStatistic extends BaseStatistic implements MergeAble<AlarmSslStatistic> {
    @Override
    public AlarmSslStatistic merge(AlarmSslStatistic alarmSslStatistic) {
        return this;
    }
}
