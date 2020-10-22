package com.tincery.gaea.api.dm;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.awt.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private List<MinMax> ipRanges;

    // 出入站相关

    private List<OutInputFilter> blackList;
    private List<OutInputFilter> whiteList;

    // 安全策略相关

    List<AssetCondition> assetConditions;



    @Setter
    @Getter
    private static class MinMax{
        private Long minIp;
        private Long maxIp;
    }

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

    public static void main(String[] args) {
        AssetConfigDO configDO = new AssetConfigDO();
        configDO.setId("id").setActivity(true).setUnit("单位").setName("资产名称")
                .setLevel(2).setRemark("备注").setIps(Arrays.asList(11L,15L)).setIpRanges(Arrays.asList(new MinMax().setMinIp(1L).setMaxIp(5L),new MinMax().setMinIp(10L).setMaxIp(20L)))
                .setType("资产类型");
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(new Protocol().setType(1).setPorts(Arrays.asList(1,2,3)));
        OutInputFilter filter = new OutInputFilter();
        filter.setMaxIp(40L).setMinIp(40L).setUnique(true).setProtocols(protocols);
        OutInputFilter filter1 = new OutInputFilter();
        filter1.setMaxIp(40L).setMinIp(40L).setUnique(true).setProtocols(protocols).setIn(true);
        OutInputFilter filter2 = new OutInputFilter();
        filter2.setMaxIp(40L).setMinIp(40L).setUnique(true).setProtocols(protocols).setIn(false);
        OutInputFilter filter3 = new OutInputFilter();
        filter3.setMaxIp(40L).setMinIp(40L).setUnique(true).setProtocols(protocols).setIn(true);
        configDO.setBlackList(Arrays.asList(filter,filter1));
        configDO.setWhiteList(Arrays.asList(filter2,filter3));


        AssetCondition assetCondition = new AssetCondition();
        AssetCondition.Condition condition = new AssetCondition.Condition();
        condition.setField("name").setValue("zhangsan").setOperator(1).setType(2);

        AssetCondition.Condition condition1 = new AssetCondition.Condition();
        condition.setField("serverIp").setValue("1.1.1.1").setOperator(1).setType(2);

        AssetCondition.Condition condition2 = new AssetCondition.Condition();
        condition.setField("clientIp").setValue("1.1.1.1").setOperator(1).setType(2);

        assetCondition.setActivity(true).setBlackList(true).setProname("ssl")
                .setConditionGroup(Arrays.asList(condition,condition1));

        AssetCondition assetCondition1  = new AssetCondition();
        assetCondition1.setProname("ipsec").setBlackList(true).setActivity(true)
                .setConditionGroup(Arrays.asList(condition2));

        configDO.setAssetConditions(Arrays.asList(assetCondition,assetCondition1));
        System.out.println(((JSON)JSON.toJSON(configDO)).toJSONString());
    }


}
