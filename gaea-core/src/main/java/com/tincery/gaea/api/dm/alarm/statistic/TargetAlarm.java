package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TargetAlarm implements Serializable, MergeAble<TargetAlarm> {
    List<OppositeIp> oppositeIpList;
    private String name;
    private String categoryDescription;
    private String subCategoryDescription;
    private String title;
    private String level;
    private Long count;
    private Boolean isSystem;

    public TargetAlarm(String categoryDescription, String subCategoryDescription, String title,
                       String level, Long count, Boolean isSystem, OppositeIp oppositeIp) {
        this.categoryDescription = categoryDescription;
        this.subCategoryDescription = subCategoryDescription;
        this.title = title;
        this.name = categoryDescription + "." + subCategoryDescription + "." + title;
        this.level = level;
        this.count = count;
        this.isSystem = isSystem;
        this.oppositeIpList = new ArrayList<>();
        this.oppositeIpList.add(oppositeIp);
    }

    public TargetAlarm() {
    }

    @Override
    public TargetAlarm merge(TargetAlarm that) {
        this.count += that.count;
        Map<String, OppositeIp> oppositeIpMap = new HashMap<>();
        this.oppositeIpList.forEach(oppositeIp -> oppositeIpMap.merge(oppositeIp.getId(), oppositeIp, (k, v) -> v.merge(oppositeIp)));
        that.oppositeIpList.forEach(oppositeIp -> oppositeIpMap.merge(oppositeIp.getId(), oppositeIp, (k, v) -> v.merge(oppositeIp)));
        this.oppositeIpList = new ArrayList<>(oppositeIpMap.values());
        return this;
    }

    @Override
    public String getId() {
        return this.name;
    }

}
