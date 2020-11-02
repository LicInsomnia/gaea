package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.util.CsvUtils;
import org.springframework.stereotype.Component;

@Component
public class AssetCsvFilter implements CsvFilter {

    private final AssetDetector assetDetector;

    public AssetCsvFilter(AssetDetector assetDetector) {
        this.assetDetector = assetDetector;
    }

    @Override
    public boolean filter(CsvRow csvRow) {
        if (!CsvUtils.hasPayload(csvRow)) {
            return false;
        }
        int protocol = csvRow.getIntegerOrDefault(HeadConst.FIELD.PROTOCOL, 0);
        /* 剔除无SYN TCP会话 */
        boolean syn = csvRow.getBoolean(HeadConst.FIELD.SYN_FLAG);
        if (protocol == 6 && !syn) {
            return false;
        }
        int assetFlag = this.assetDetector.getAssetFlag(csvRow.get(HeadConst.FIELD.CLIENT_IP), csvRow.get(HeadConst.FIELD.SERVER_IP));
        if (assetFlag == 0) {
            return false;
        }
        csvRow.putExtension(HeadConst.FIELD.ASSET_FLAG, assetFlag);
        return true;
    }
}
