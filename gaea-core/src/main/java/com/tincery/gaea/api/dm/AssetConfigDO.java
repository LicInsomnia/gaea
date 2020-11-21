package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.IpGroup;
import com.tincery.gaea.api.base.IpHitable;
import com.tincery.gaea.api.base.IpRange;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

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
    private String type;
    private String remark;

    private String address;
    private List<String> pids;

    private Boolean activity;
    private List<Long> ips;
    private List<IpRange> ipRanges;

    // 出入站相关

    private BlackOrWhiteList blackList;
    private BlackOrWhiteList whiteList;

    // 安全策略相关
    // 一组中有多个条件

    private List<AssetCondition> assetStrategyCondition;

    // 证书

    private AssetCondition assetCertStrategy;


    public void strategyHit(JSONObject assetServerJson) {
        if (CollectionUtils.isEmpty(assetStrategyCondition)) {
            return;
        }
        String proName = assetServerJson.getString(HeadConst.FIELD.PRONAME);
        AssetCondition target = null;
        for (AssetCondition assetCondition : assetStrategyCondition) {
            if (Objects.equals(proName, assetCondition.getProname())) {
                target = assetCondition;
                break;
            }
        }
        if (target == null) {
            return;
        }
        for (AssetCondition.ConditionGroup conditionGroup : target.getConditionGroup()) {
            if(conditionGroup.hit(assetServerJson,assetCertStrategy)){
                assetServerJson.put("$description",conditionGroup.getDescription());
                assetServerJson.put("alarm",true);
                return;
            }
        }

    }

    /****
     * 黑白名单列表
     * 黑白名单中都含有一个 in 入站规则   out 出站规则
     **/
    @Setter
    @Getter
    public static class BlackOrWhiteList {
        private OutInputFilter in;
        private OutInputFilter out;
    }

    /**
     * 出入站规则 包含一个境外规则和多个境内规则
     **/
    @Setter
    @Getter
    public static class OutInputFilter {
        private List<DomesticFilter> domestic;
        private OverseasFilter overseas;
    }

    /**
     * 境内规则  一个IP或者IP段下可配置多个协议
     **/
    @Setter
    @Getter
    public static class DomesticFilter implements IpHitable {
        private boolean unique;
        private Long minIp;
        private Long maxIp;
        private List<ProtocolGroup> protocols;

        @Override
        public boolean hit(long ip, int protocol, int port) {
            if (!checkIp(ip)) {
                return false;
            }
            if (CollectionUtils.isEmpty(protocols)) {
                return true;
            }
            return protocols.stream().anyMatch(protocolItem -> protocolItem.checkProtocolAndPort(protocol, port));
        }

        /****
         * IP是否符合规则
         * @param ip ip
         * @return boolean 是否符合
         **/
        private boolean checkIp(long ip) {
            if (unique) {
                return Objects.equals(minIp, ip);
            } else {
                return ip >= minIp && ip <= maxIp;
            }
        }
    }

    /**
     * 境外规则   包含被被移除的IP、协议、端口组
     * 也可移除内容
     **/
    @Setter
    @Getter
    public static class OverseasFilter implements IpHitable {
        private List<IpGroup> exclusions;

        /****
         * 境外规则是否命中  如果在被忽略的内容中  则不会被命中
         **/
        @Override
        public boolean hit(long ip, int protocol, int port) {
            if (CollectionUtils.isEmpty(exclusions)) {
                return true;
            }
            return exclusions.stream().anyMatch(ipGroup -> ipGroup.hit(ip, protocol, port));
        }
    }


}
