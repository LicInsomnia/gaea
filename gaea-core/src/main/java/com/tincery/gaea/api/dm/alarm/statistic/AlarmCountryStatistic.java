package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Insomnia
 */
@Data
public class AlarmCountryStatistic extends BaseStatistic implements MergeAble<AlarmCountryStatistic> {

    private String country;
    private LocalDateTime timeTag;
    private Long count;

    @Override
    public AlarmCountryStatistic merge(AlarmCountryStatistic that) {
        this.count += that.count;
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.country + "." + this.timeTag;
    }

}
