package com.tincery.gaea.core.base.component.support;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.DnsRequestBO;
import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.config.RunConfig;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileReader;
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
    public void init() {
        if (RunConfig.isEmpty()) {
            return;
        }
        Date startTime = RunConfig.getDate("startTime");
        long startTimeLong;
        if (startTime == null) {
            startTimeLong = DateUtils.LocalDateTime2Long(LocalDateTime.now().minusHours(3));
        } else {
            startTimeLong = startTime.getTime() - (3 * DateUtils.HOUR);
        }
        String category = "dnsRequest";
        String impDnsRequestPath = NodeInfo.getDataWarehouseCsvPathByCategory(category);
        List<File> files = FileUtils.searchFiles(impDnsRequestPath, category, null, null, 0);
        for (File file : files) {
            long time = Long.parseLong(file.getName().split("\\.")[0].split("_")[1]);
            if (time > startTimeLong) {
                List<String> lines = FileReader.readLine(file);
                List<DnsRequestBO> list = new ArrayList<>();
                for (String line : lines) {
                    DnsRequestBO dnsRequestBO = JSONObject.parseObject(line, DnsRequestBO.class);
                    if (null != dnsRequestBO) {
                        list.add(dnsRequestBO);
                    }
                }
                this.dnsRequestMap = list.stream().collect(Collectors.groupingBy(DnsRequestBO::getKey));
            }
        }
        if (CollectionUtil.isNotEmpty(this.dnsRequestMap)) {
            this.dnsRequestMap.values().forEach(Collections::sort);
            this.empty = false;
        }
    }

    public void append(DnsData dnsData) {
        if (dnsData.getImp() || dnsData.getDataType() != 1) {
            return;
        }
        String domain = dnsData.getDnsExtension().getDomain();
        Set<String> responseIps = dnsData.getDnsExtension().getResponseIp();
        if (null == domain || null == responseIps) {
            return;
        }
        for (String responseIp : responseIps) {
            String key = dnsData.getUserId() + "_" + responseIp;
            DnsRequestBO dnsRequestBO = new DnsRequestBO(key, domain, dnsData.getCapTime());
            List<DnsRequestBO> dnsRequestBOList;
            if (this.dnsRequestMap.containsKey(key)) {
                dnsRequestBOList = this.dnsRequestMap.get(key);
                dnsRequestBOList.add(dnsRequestBO);
            } else {
                dnsRequestBOList = new ArrayList<>();
                dnsRequestBOList.add(dnsRequestBO);
                this.dnsRequestMap.put(key, dnsRequestBOList);
            }
        }
    }

}
