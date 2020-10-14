package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.AssetConfigDO;
import com.tincery.gaea.core.base.dao.AssetConfigDao;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.starter.base.InitializationRequired;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gongxuanzhang
 */

@Component
@Slf4j
public class AssetDetector implements InitializationRequired {


    @Autowired
    private AssetConfigDao assetConfigDao;
    /**
     * assetFlg = 0:不是资产
     * assetFlg = 1:客户端资产
     * assetFlg = 2:服务端资产
     * assetFlg = 3:都是资产
     */
    private final Map<String, AssetConfigDO> uniqueAssetMap = new HashMap<>();

    private final Map<Pair<Long, Long>, AssetConfigDO> rangeAssetMap = new HashMap<>();

    public boolean checkAssetIp(String ip) {
        if (this.uniqueAssetMap.containsKey(ip)) {
            return true;
        }
        long ipN = ToolUtils.IP2long(ip);
        for (Pair<Long, Long> pair : this.rangeAssetMap.keySet()) {
            Long minIp = pair.getKey();
            Long maxIp = pair.getValue();
            if (minIp <= ipN && ipN <= maxIp) {
                return true;
            }
        }
        return false;
    }

    /**
     * assetFlag = 0:不是资产 assetFlag = 1:客户端资产 assetFlag = 2:服务端资产 assetFlag = 3:都是资产
     */
    public int checkSessionAsset(String clientIp, String serverIp) {
        int assetFlag = 0;
        assetFlag += checkAssetIp(clientIp) ? 1 : 0;
        assetFlag += checkAssetIp(serverIp) ? 2 : 0;
        return assetFlag;
    }

    public AssetConfigDO getAsset(String ip) {
        if (this.uniqueAssetMap.containsKey(ip)) {
            return this.uniqueAssetMap.get(ip);
        }
        Long ipN = ToolUtils.IP2long(ip);
        for (Pair<Long, Long> pair : this.rangeAssetMap.keySet()) {
            if (pair.getKey() <= ipN && ipN <= pair.getValue()) {
                return this.rangeAssetMap.get(pair);
            }
        }
        return null;
    }


    @Override
    public void init() {
        List<AssetConfigDO> activityData = assetConfigDao.getActivityData();
        activityData.forEach(this::putAsset);
        log.info("成功加载{}条独立IP资产规则，{}条范围资产规则",this.uniqueAssetMap.size(),this.rangeAssetMap.size());
    }
    private void putAsset(AssetConfigDO asset){
        if (asset.isUnique()) {
            this.uniqueAssetMap.put(asset.getIp(), asset);
        } else {
            this.rangeAssetMap.put(new Pair<>(asset.getMinIp_n(), asset.getMaxIp_n()), asset);
        }
    }


}
