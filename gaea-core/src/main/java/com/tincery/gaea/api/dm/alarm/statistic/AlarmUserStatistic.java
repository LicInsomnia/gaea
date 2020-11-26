package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmUserStatistic extends BaseStatistic implements MergeAble<AlarmUserStatistic> {

    @Override
    public AlarmUserStatistic merge(AlarmUserStatistic alarmUserStatistic) {
        return this;
    }

}
