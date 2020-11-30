package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Insomnia
 */
@Data
public class AlarmCountStatistic extends BaseStatistic implements MergeAble<AlarmCountStatistic> {

    LocalDateTime timeTag;
    Long count;

    @Override
    public AlarmCountStatistic merge(AlarmCountStatistic that) {
        this.count += that.count;
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.timeTag.toString();
    }

}
