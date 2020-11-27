package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Insomnia
 */
@Data
public class TargetStatistic implements Serializable {

    private final String id;
    private final String targetType;
    private final LocalDateTime insertTime;
    private final LocalDateTime updateTime;
    private final Long count;
    private final Set<String> msisdn = new HashSet<>();
    private final Set<String> imei = new HashSet<>();

    public TargetStatistic(Alarm alarm) {
        if (null != alarm.getTargetName()) {
            this.id = alarm.getTargetName();
            this.targetType = "impTarget";
        } else if (null != alarm.getImsi()) {
            this.id = alarm.getImsi();
            this.targetType = "imsi";
        } else if (null != alarm.getAssetIp()) {
            this.id = alarm.getAssetIp();
            this.targetType = "asset";
        } else {
            this.id = alarm.getClientIp();
            this.targetType = "clientIp";
        }
        this.insertTime = this.updateTime = DateUtils.Long2LocalDateTime(alarm.getCapTime());
        this.count = 1L;
        if (null != alarm.getImei()) {
            this.imei.add(alarm.getImei());
        }
        if (null != alarm.getMsisdn()) {
            this.msisdn.add(alarm.getMsisdn());
        }
    }

}
