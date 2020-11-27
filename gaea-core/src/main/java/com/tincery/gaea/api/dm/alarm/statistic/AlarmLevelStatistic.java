package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.api.base.KV;
import com.tincery.gaea.core.base.component.support.AlarmStatisticSupport;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Insomnia
 */
@Data
public class AlarmLevelStatistic extends BaseStatistic implements MergeAble<AlarmLevelStatistic> {

    private LocalDateTime timeTag;
    private List<KV<String, Long>> levelCountList;

    @Override
    public AlarmLevelStatistic merge(AlarmLevelStatistic that) {
        this.levelCountList = AlarmStatisticSupport.mergekv(this.levelCountList, that.levelCountList);
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.timeTag.toString();
    }
}
