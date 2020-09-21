package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gxz
 * @date 2019/10/25
 */
@Setter
@Getter
@ToString
public class AssetConfigDO extends SimpleBaseDO {

    private static final String RANGE_TYPE = "range";
    private static final String UNIQUE_TYPE = "unique";



    private AlertConfig alertConfig;
    @Id
    private String id;
    private AssetConfigInfo info;
    private String type;
    private Boolean activity;
    private Long minIp_n;
    private Long maxIp_n;
    private String minIp;
    private String maxIp;
    private Integer protocol;
    private Integer port;
    private String keyword;
    private String ip;
    private LocalDateTime starttime;

    public final boolean isUnique() {
        return "unique".equals(this.type);
    }

    public final String getIp() {
        if (isUnique()) {
            return this.ip;
        } else {
            return this.minIp + "-" + this.maxIp;
        }
    }


    @Setter
    @Getter
    public static class AlertConfig {
        private Boolean overseasLimit;
        private List<String> overseasTable;
        private Boolean stepInLimit;
        private List<String> stepInEnableTable;
        private Boolean stepOutLimit;
        private List<String> stepOutEnableTable;
        private Boolean serverPortLimit;
        private List<Integer> legalPort;
    }

    @Setter
    @Getter
    public static class AssetConfigInfo{
        private String unit;
        private String remarks;
        private String name;
        private Integer level;
        private String prototype;
    }


}
