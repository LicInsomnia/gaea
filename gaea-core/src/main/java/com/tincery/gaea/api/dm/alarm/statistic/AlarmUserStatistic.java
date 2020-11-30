package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.util.Set;

/**
 * @author Insomnia
 */
@Data
public class AlarmUserStatistic extends BaseStatistic implements MergeAble<AlarmUserStatistic> {

    private String categoryDescription;
    private String subCategoryDescription;
    private String title;
    private String type;
    private Set<String> user;
    private Integer userCount;
    private Set<String> asset;
    private Integer assetCount;

    @Override
    public AlarmUserStatistic merge(AlarmUserStatistic that) {
        if (null == this.user) {
            this.user = that.user;
        } else if (null != that.user) {
            this.user.addAll(that.user);
        }
        if (null == this.asset) {
            this.asset = that.asset;
        } else if (null != that.asset) {
            this.asset.addAll(that.asset);
        }
        this.userCount = null == this.user ? 0 : this.user.size();
        this.assetCount = null == this.asset ? 0 : this.asset.size();
        return this;
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.categoryDescription + "." +
                this.subCategoryDescription + "." + this.title + "." + this.type;
    }
}
