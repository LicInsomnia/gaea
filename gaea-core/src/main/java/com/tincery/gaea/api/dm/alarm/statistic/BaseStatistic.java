package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author Administrator
 */
@Data
public abstract class BaseStatistic extends SimpleBaseDO {

    @Id
    protected String id;

    public abstract void setId();

}
