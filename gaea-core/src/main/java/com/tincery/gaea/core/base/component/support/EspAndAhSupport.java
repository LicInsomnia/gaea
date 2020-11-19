package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Insomnia
 */
public class EspAndAhSupport {

    private final Map<String, Long> espMap = new HashMap<>();
    private final Map<String, Long> ahMap = new HashMap<>();

    public void initialize(List<String> espAndAhPaths) {
        for (String fileName : espAndAhPaths) {
            CsvReader csvReader;
            try {
                csvReader = CsvReader.builder().file(fileName).build();
                CsvRow csvRow;
                while ((csvRow = csvReader.nextRow()) != null) {
                    String proName = csvRow.get(HeadConst.FIELD.PRONAME);
                    Long capTime = csvRow.getLong(HeadConst.FIELD.CAPTIME);
                    Long clientIpN = csvRow.getLong(HeadConst.FIELD.CLIENT_IP_N);
                    Long serverIpN = csvRow.getLong(HeadConst.FIELD.SERVER_IP_N);
                    String targetName = csvRow.get(HeadConst.FIELD.TARGET_NAME);
                    String key = targetName + "_" + Math.min(clientIpN, serverIpN) + "_" + Math.max(clientIpN, serverIpN);
                    switch (proName) {
                        case HeadConst.PRONAME.ESP:
                            appendEsp(key, capTime);
                            continue;
                        case HeadConst.PRONAME.AH:
                            appendAh(key, capTime);
                            continue;
                        default:
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    public String checkEncryptionMessageProtocol(AbstractDataWarehouseData data) {
        String targetName = data.getTargetName();
        if (null == targetName) {
            targetName = "";
        }
        Long clientIpN = data.getClientIpN();
        Long serverIpN = data.getServerIpN();
        String key = targetName + "_" + Math.min(clientIpN, serverIpN) + "_" + Math.max(clientIpN, serverIpN);
        Long capTime = data.getCapTime();
        boolean esp = false;
        boolean ah = false;
        Long time;
        time = this.espMap.get(key);
        if (null != time && time > capTime) {
            esp = true;
        }
        time = this.ahMap.get(key);
        if (null != time && time > capTime) {
            ah = true;
        }
        if (esp && ah) {
            return "ESP&AH";
        } else if (esp) {
            return "ESP";
        } else if (ah) {
            return "AH";
        } else {
            return null;
        }
    }

    public void clear() {
        this.espMap.clear();
        this.ahMap.clear();
    }

    private void appendEsp(String key, Long capTime) {
        if (this.espMap.containsKey(key)) {
            Long time = this.ahMap.get(key);
            this.ahMap.replace(key, Math.max(time, capTime));
        } else {
            this.espMap.put(key, capTime);
        }
    }

    private void appendAh(String key, Long capTime) {
        if (this.ahMap.containsKey(key)) {
            Long time = this.ahMap.get(key);
            this.ahMap.replace(key, Math.max(time, capTime));
        } else {
            this.ahMap.put(key, capTime);
        }
    }

}
