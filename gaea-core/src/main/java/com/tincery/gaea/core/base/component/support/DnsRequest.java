package com.tincery.gaea.core.base.component.support;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.DnsRequestBO;
import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DnsRequest implements InitializationRequired {

    private static final String CATEGORY = "dnsRequest";
    private boolean empty = true;
    private Map<String, List<DnsRequestBO>> dnsRequestMap = new ConcurrentHashMap<>();
    private Long minTime = Long.MAX_VALUE;
    private String dnsRequestPath;

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
        this.dnsRequestPath = NodeInfo.getDataWarehouseCsvPathByCategory(CATEGORY);
        FileUtils.checkPath(this.dnsRequestPath);
        long startTime = DateUtils.LocalDateTime2Long(LocalDateTime.now().minusHours(3));
        List<File> files = FileUtils.searchFiles(dnsRequestPath, CATEGORY, null, ".json", 0);
        for (File file : files) {
            long time = Long.parseLong(file.getName().split("\\.")[0].split("_")[1]);
            if (time > startTime) {
                List<String> lines = FileUtils.readLine(file);
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
        if ((dnsData.getImp() != null && dnsData.getImp()) || dnsData.getDataType() != 1) {
            return;
        }
        String domain = dnsData.getDnsExtension().getDomain();
        Set<String> responseIps = dnsData.getDnsExtension().getResponseIp();
        if (null == domain || null == responseIps) {
            return;
        }
        for (String responseIp : responseIps) {
            String key = dnsData.getUserId() + "_" + responseIp;
            Long capTimeN = dnsData.getCapTime();
            DnsRequestBO dnsRequestBO = new DnsRequestBO(key, domain, capTimeN);
            List<DnsRequestBO> dnsRequestBOList;
            if (this.dnsRequestMap.containsKey(key)) {
                dnsRequestBOList = this.dnsRequestMap.get(key);
                dnsRequestBOList.add(dnsRequestBO);
            } else {
                dnsRequestBOList = new ArrayList<>();
                dnsRequestBOList.add(dnsRequestBO);
                this.dnsRequestMap.put(key, dnsRequestBOList);
            }
            this.minTime = Math.min(this.minTime, capTimeN);
        }
        this.empty = false;
    }

    public void output() {
        if (this.empty) {
            return;
        }
        String outputFile = this.dnsRequestPath + "/" + CATEGORY + "_" + this.minTime + ".json";
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            for (List<DnsRequestBO> dnsRequestBOList : this.dnsRequestMap.values()) {
                for (DnsRequestBO dnsRequestBO : dnsRequestBOList) {
                    fileWriter.write(JSONObject.toJSONString(dnsRequestBO));
                }
            }
        }
        this.dnsRequestMap.clear();
        this.minTime = Long.MAX_VALUE;
        this.empty = true;
    }

}
