package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.api.dm.alarm.Alarm;
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

    public AlarmStatistic(Alarm alarm) {
        this.categoryDescription = alarm.getCategoryDesc();
        this.subCategoryDescription = alarm.getSubCategoryDesc();
        this.title = alarm.getTitle();
        this.type = alarm.getType();
        this.creator = alarm.getCreateUser();
        this.level = alarm.getLevel();
        this.count = 1;
        this.id = this.creator + "." + this.type + "." + this.categoryDescription + "." +
                this.subCategoryDescription + "." + this.title;
    }

    @Override
    public AlarmStatistic merge(AlarmStatistic that) {
        this.count += that.count;
        return this;
    }

}
