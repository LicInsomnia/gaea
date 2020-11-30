package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.api.base.KV;
import com.tincery.gaea.core.base.component.support.AlarmStatisticSupport;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Insomnia
 */
@Data
public class AlarmCategoryStatistic extends BaseStatistic implements MergeAble<AlarmCategoryStatistic> {

    private List<KV<LocalDateTime, Long>> timeCountStatisticList;
    private String categoryDescription;
    private String subCategoryDescription;
    private long totalCount;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;

    @Override
    public AlarmCategoryStatistic merge(AlarmCategoryStatistic that) {
        this.timeCountStatisticList = AlarmStatisticSupport.mergekv(this.timeCountStatisticList, that.timeCountStatisticList);
        this.totalCount += that.totalCount;
        this.insertTime = DateUtils.min(this.insertTime, that.insertTime);
        this.updateTime = DateUtils.max(this.updateTime, that.updateTime);
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + categoryDescription + "." + subCategoryDescription;
    }
}
