package com.tincery.gaea.core.base.tool.util;

import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;

public class CsvUtils {

    private CsvUtils() {
        throw new RuntimeException("工具类进制创建实体");
    }
    /**
     * 判别会话数据载荷形式，只保留TCP双方都有载荷、UCP至少单方有载荷的或其它传输层协议的会话数据
     */
    public static boolean hasPayload(CsvRow csvRow) {
        int protocol = csvRow.getIntegerOrDefault(HeadConst.CSV.PROTOCOL, 0);
        long upByte = csvRow.getLongOrDefault(HeadConst.CSV.UP_BYTE, 0L);
        long downByte = csvRow.getLongOrDefault(HeadConst.CSV.DOWN_BYTE, 0L);
        switch (protocol) {
            // TCP
            case 6:
                if (upByte == 0 || downByte == 0) {
                    return false;
                }
                break;
            // UDP
            case 17:
                if (upByte == 0 && downByte == 0) {
                    return false;
                }
                break;
            // 其它协议
            default:
                break;
        }
        return true;
    }

}
