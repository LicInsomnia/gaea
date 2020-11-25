package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.IpRange;
import com.tincery.gaea.api.dm.AssetConfigDO;
import com.tincery.gaea.core.base.dao.AssetConfigDao;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.starter.base.InitializationRequired;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gongxuanzhang
 */

@Component
@Slf4j
public class AssetDetector implements InitializationRequired {


    /**
     * assetFlag = 0:不是资产
     * assetFlag = 1:客户端资产
     * assetFlag = 2:服务端资产
     * assetFlag = 3:都是资产
     */
    private final Map<Long, AssetConfigDO> uniqueAssetMap = new HashMap<>();
    private final Map<Pair<Long, Long>, AssetConfigDO> rangeAssetMap = new HashMap<>();
    @Autowired
    private AssetConfigDao assetConfigDao;

    /****
     * 匹配IP是否是资产
     * @author gxz
     * @param ip  IP
     * @return boolean 是否是资产
     **/
    public boolean checkAssetIp(String ip) {
        long ipN = ToolUtils.IP2long(ip);
        return checkAssetIp(ipN);
    }

    public boolean checkAssetIp(long ip) {
        if (this.uniqueAssetMap.containsKey(ip)) {
            return true;
        }
        for (Pair<Long, Long> pair : this.rangeAssetMap.keySet()) {
            Long minIp = pair.getKey();
            Long maxIp = pair.getValue();
            if (minIp <= ip && ip <= maxIp) {
                return true;
            }
        }
        return false;
    }


    /**
     * assetFlag = 0:不是资产
     * assetFlag = 1:客户端资产
     * assetFlag = 2:服务端资产
     * assetFlag = 3:都是资产
     */
    public int getAssetFlag(String clientIp, String serverIp) {
        int assetFlag = 0;
        assetFlag += checkAssetIp(clientIp) ? 1 : 0;
        assetFlag += checkAssetIp(serverIp) ? 2 : 0;
        return assetFlag;
    }

    public AssetConfigDO getAsset(String ip) {
        Long ipN = ToolUtils.IP2long(ip);
        return getAsset(ipN);
    }

    public AssetConfigDO getAsset(long ipN) {
        if (this.uniqueAssetMap.containsKey(ipN)) {
            return this.uniqueAssetMap.get(ipN).setRange(false);
        }
        for (Pair<Long, Long> pair : this.rangeAssetMap.keySet()) {
            if (pair.getKey() <= ipN && ipN <= pair.getValue()) {
                return this.rangeAssetMap.get(pair).setRange(true);
            }
        }
        return null;
    }


    @Override
    public void init() {
        List<AssetConfigDO> activityData = assetConfigDao.getActivityData();
        activityData.forEach(this::putAsset);
        log.info("成功加载{}条独立IP资产规则，{}条范围资产规则", this.uniqueAssetMap.size(), this.rangeAssetMap.size());
    }

    private void putAsset(AssetConfigDO assetConfig) {
        List<Long> ips = assetConfig.getIps();
        List<IpRange> ipRanges = assetConfig.getIpRanges();
        if (!CollectionUtils.isEmpty(ips)) {
            ips.forEach(ip -> this.uniqueAssetMap.put(ip, assetConfig));
        }

        if (!CollectionUtils.isEmpty(ipRanges)) {
            ipRanges.forEach(ipRange -> this.rangeAssetMap.put(new Pair<>(ipRange.getMinIp(), ipRange.getMaxIp()),
                    assetConfig));
        }
    }

}
