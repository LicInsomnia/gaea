package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmStatistic extends BaseStatistic implements MergeAble<AlarmStatistic> {

    private String categoryDescription;
    private String subCategoryDescription;
    private String title;
    private String type;
    private String creator;
    private String level;
    private Integer count;

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.creator + "." + this.type + "." +
                this.categoryDescription + "." + this.subCategoryDescription + "." + this.title;
    }

    @Override
    public AlarmStatistic merge(AlarmStatistic that) {
        this.count += that.count;
        return this;
    }

}
