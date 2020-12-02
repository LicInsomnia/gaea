package com.tincery.gaea.api.dm.alarm.statistic;

import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Insomnia
 */
@Data
public class OppositeIp implements Serializable, MergeAble<OppositeIp> {

    private String ip;
    private String country;
    private LocalDateTime updateTime;
    private String level;
    private String color;
    private Long count;

    public OppositeIp() {

    }

    public OppositeIp(String ip, String country, LocalDateTime updateTime) {
        this.ip = ip;
        this.country = country;
        this.updateTime = updateTime;
    }

    public OppositeIp(String ip, String country, String level, Long count) {
        this.ip = ip;
        this.country = country;
        this.level = level;
        switch (this.level) {
            case "紧急":
                this.color = "#860000";
                break;
            case "严重":
                this.color = "#FF0000";
                break;
            case "一般":
                this.color = "#FF8040";
                break;
            default:
                this.color = "#FFD700";
                break;
        }
        this.count = count;
    }

    @Override
    public OppositeIp merge(OppositeIp that) {
        if (null != this.updateTime) {
            this.updateTime = DateUtils.max(this.updateTime, that.updateTime);
        }
        if (null != this.count) {
            this.count += that.count;
        }
        return this;
    }

    @Override
    public String getId() {
        return this.ip;
    }
}
