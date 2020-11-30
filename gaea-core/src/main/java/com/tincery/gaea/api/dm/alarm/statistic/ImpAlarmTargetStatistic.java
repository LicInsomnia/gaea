package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class ImpAlarmTargetStatistic extends BaseStatistic implements MergeAble<ImpAlarmTargetStatistic> {

    @Override
    public void setId() {

    }

    @Override
    public ImpAlarmTargetStatistic merge(ImpAlarmTargetStatistic impAlarmTargetStatistic) {
        return this;
    }
}
