package com.tincery.gaea.api.dm;

import com.tincery.gaea.api.base.IpRange;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.util.*;

/**
 * @author gxz
 * @date 2019/10/25
 */
@Setter
@Getter
@ToString
public class AssetConfigDO extends SimpleBaseDO {



    @Id
    private String id;

    // 基本信息

    private String unit;
    private String name;
    private Integer level;
    private String remark;

    private Boolean activity;
    private String type;
    private List<Long> ips;
    private List<IpRange> ipRanges;

    // 出入站相关

    private List<OutInputFilter> blackList;
    private List<OutInputFilter> whiteList;

    // 安全策略相关
    // 一组中有多个条件

    List<List<AssetCondition>> assetStrategyConditionGroup;




    /**
     * 出入站规则  一个IP或者IP段下可配置多个协议
     **/
    @Setter
    @Getter
    public static class OutInputFilter{
        private boolean in;
        private boolean unique;
        private Long minIp;
        private Long maxIp;
        private List<Protocol> protocols;
    }


    /**
     * 协议配置   一个协议下可配置多个端口
     *
     **/
    @Setter
    @Getter
    private static class Protocol{
        private int type;
        private List<Integer> ports;
    }

}
