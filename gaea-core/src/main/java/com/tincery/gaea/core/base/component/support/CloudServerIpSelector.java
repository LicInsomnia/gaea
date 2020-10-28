package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.CloudConfigDO;
import com.tincery.gaea.core.base.dao.CloudConfigDao;
import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CloudServerIpSelector implements InitializationRequired {

    static int a = 0;
    private CloudServerIp[] services;

    @Autowired
    private CloudConfigDao cloudConfigDao;

    public String getCloudService(String ip) {
        long ipN = ToolUtils.IP2long(ip);
        int left = 0;
        int right = this.services.length - 1;
        int middle = (left + right) / 2;
        while (left <= right) {
            int result = services[middle].compare(ipN);
            if (result == 0) {
                return services[middle].service;
            } else if (result < 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
            middle = (left + right) / 2;
        }
        return null;
    }


    @Override
    public void init() {
        List<CloudConfigDO> all = cloudConfigDao.findAll();
        if (CollectionUtils.isEmpty(all)) {
            throw new InitException("cloud_config 为空");
        }
        List<CloudServerIp> collect = all.stream().map(CloudServerIp::new)
                .filter(cloud -> cloud.ip != -1).sorted().collect(Collectors.toList());
        this.services = collect.toArray(new CloudServerIp[]{});
        log.info("cloud_config 加载了{}组条件", services.length);
    }

    private static class CloudServerIp implements Comparable<CloudServerIp> {
        long ip;
        int mask;
        String service;

        CloudServerIp(CloudConfigDO cloudConfigDO) throws NumberFormatException {
            String id = cloudConfigDO.getId();
            try {
                if (id.contains("/")) {
                    this.ip = ToolUtils.IP2long(id.split("/")[0]);
                    this.mask = 32 - Integer.parseInt(id.split("/")[1]);
                } else {
                    this.ip = ToolUtils.IP2long(id);
                    this.mask = 32;
                    this.service = cloudConfigDO.getService();
                }
            } catch (Exception ignore) {
                this.ip = -1;
                log.error("遇到cloud_config表初始化问题{}", id + "无法解析");
            }


        }


        @Override
        public int compareTo(CloudServerIp o) {
            return compare(o.ip);

        }

        public int compare(long ipN) {
            return Long.compareUnsigned(this.ip, ipN >> this.mask << this.mask);
        }
    }

}
