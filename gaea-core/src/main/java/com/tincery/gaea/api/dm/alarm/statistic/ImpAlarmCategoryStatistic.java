package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Insomnia
 */
@Data
public class ImpAlarmCategoryStatistic extends BaseStatistic implements MergeAble<ImpAlarmCategoryStatistic> {

    private String categoryDescription;
    private String subCategoryDescription;
    private String title;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
    private List<TargetStatistic> targets;

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.categoryDescription + "." + this.subCategoryDescription + "." + this.title;
    }

    @Override
    public ImpAlarmCategoryStatistic merge(ImpAlarmCategoryStatistic impAlarmCategoryStatistic) {
        return this;
    }
}
