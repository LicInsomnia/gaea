package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvFilter;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.util.CsvUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ImpSessionCsvFilter implements CsvFilter {

    @Override
    public boolean filter(CsvRow csvRow) {
        if (!CsvUtils.hasPayload(csvRow)) {
            return false;
        }
        return isImpSession(csvRow);
    }

    private boolean isImpSession(CsvRow csvRow) {
        return StringUtils.isNotEmpty(csvRow.get(HeadConst.FIELD.TARGET_NAME));
    }
}
