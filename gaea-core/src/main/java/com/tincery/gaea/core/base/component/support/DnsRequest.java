package com.tincery.gaea.core.base.component.support;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.DnsRequestBO;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DnsRequest implements InitializationRequired {


    private Map<String, List<DnsRequestBO>> dnsRequestMap;
    private boolean empty = true;

    public final int size() {
        return this.dnsRequestMap.size();
    }

    public final DnsRequestBO getDnsRequest(String key, long capTime) {
        if (this.empty) {
            return null;
        }
        List<DnsRequestBO> dnsRequestList = this.dnsRequestMap.get(key);
        if (null == dnsRequestList) {
            return null;
        }
        for (DnsRequestBO dnsRequest : dnsRequestList) {
            if (capTime >= dnsRequest.getCapTime()) {
                return dnsRequest;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        JSONObject jsonObject = new JSONObject((Map) CommonConfig.get("reorganization"));
        Date startTime = jsonObject.getDate("starttime");
        long startTimeLong;
        if(startTime == null){
            startTimeLong = DateUtils.LocalDateTime2Long(LocalDateTime.now().minusHours(3));
        }else{
            startTimeLong = startTime.getTime() - (15 * DateUtils.MINUTE);
        }

        String category = "impdnsrequest";
        String impDnsRequestPath = NodeInfo.getDataWarehouseCsvPathByCategory(category);
        List<File> files = FileUtils.searchFiles(impDnsRequestPath, category, null, null, 0);
        for (File file : files) {
            long time = Long.parseLong(file.getName().split("\\.")[0].split("_")[1]);
            if (time > startTimeLong) {
                try {
                    CsvReader csvReader = CsvReader.builder().file(file).build();
                    CsvRow csvRow;
                    List<DnsRequestBO> list = new ArrayList<>();
                    while ((csvRow = csvReader.nextRow()) != null) {
                        list.add(new DnsRequestBO(csvRow.get(0), csvRow.get(1), csvRow.getLong(4)));
                    }
                    this.dnsRequestMap = list.stream().collect(Collectors.groupingBy(DnsRequestBO::getKey));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (CollectionUtil.isNotEmpty(this.dnsRequestMap)) {
            this.dnsRequestMap.values().forEach(Collections::sort);
            this.empty = false;
        }
    }
}
