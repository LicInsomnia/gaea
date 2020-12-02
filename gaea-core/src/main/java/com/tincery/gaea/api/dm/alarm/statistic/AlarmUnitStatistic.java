package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Insomnia
 */
@Data
public class AlarmUnitStatistic extends BaseStatistic implements MergeAble<AlarmUnitStatistic> {

    private String unit;
    private String level;
    private Long count;
    private Double value;
    private List<OppositeIp> oppositeIps;

    @Override
    public AlarmUnitStatistic merge(AlarmUnitStatistic that) {
        this.count += that.count;
        Map<String, OppositeIp> oppositeIpMap = new HashMap<>();
        this.oppositeIps.forEach(oppositeIp -> oppositeIpMap.merge(oppositeIp.getId(), oppositeIp, (k, v) -> v.merge(oppositeIp)));
        that.oppositeIps.forEach(oppositeIp -> oppositeIpMap.merge(oppositeIp.getId(), oppositeIp, (k, v) -> v.merge(oppositeIp)));
        this.oppositeIps = new ArrayList<>(oppositeIpMap.values());
        return this;
    }

    public void setValue(Map<String, Double> unitValueMap) {
        this.value = unitValueMap.getOrDefault(this.unit, 0.0);
    }

    @Override
    public void setId() {
        this.id = this.getClass().getSimpleName() + "." + this.unit;
    }
}
