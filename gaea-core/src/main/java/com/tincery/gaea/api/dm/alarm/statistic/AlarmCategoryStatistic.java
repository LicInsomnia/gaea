package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmCategoryStatistic extends BaseStatistic implements MergeAble<AlarmCategoryStatistic> {

    @Override
    public AlarmCategoryStatistic merge(AlarmCategoryStatistic alarmCategoryStatistic) {
        return this;
    }


}
