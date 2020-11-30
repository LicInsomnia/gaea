package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AlarmSslStatistic extends BaseStatistic implements MergeAble<AlarmSslStatistic> {

    private String subCategoryDescription;
    private Long count;

    @Override
    public AlarmSslStatistic merge(AlarmSslStatistic that) {
        this.count += that.count;
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.subCategoryDescription;
    }

}
