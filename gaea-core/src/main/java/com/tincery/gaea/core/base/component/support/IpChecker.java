package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.NetUtil;
import com.tincery.starter.base.InitializationRequired;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Insomnia
 * ip地址地理位置检测
 */
@Slf4j
@Component
public class IpChecker implements InitializationRequired {

    /**
     * 国内ip地址段
     */
    private final List<Pair<Long, Long>> ChinaIps = new ArrayList<>();


    /**
     * 内外网ip检测
     * @param ip 待检测ip
     * @return true:内网ip false:外网ip
     *
     * Class A 10.0.0.0-10.255.255.255 （167772160 - 184549375）
     * Class B 172.16.0.0-172.31.255.255（2886729728 - 2887778303）
     * Class C 192.168.0.0-192.168.255.255（3232235520 - 3232301055）
     */
    public boolean isInner(long ip) {
        return (167772160L <= ip && 184549375L >= ip) ||
                (2886729728L <= ip && 2887778303L >= ip) ||
                (3232235520L <= ip && 3232301055L >= ip);
    }

    public boolean isInner(String ip) {
        try {
            long ipN = ToolUtils.IP2long(ip);
            return isInner(ipN);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 内外网ip检测
     *
     * @param ip 待检测ip
     * @return false: 国内 true:国外 null 未知
     */
    public Boolean isForeign(Long ip) {
        if (ip.equals(-1L)) {
            return null;
        }
        for (Pair<Long, Long> ipPair : this.ChinaIps) {
            if (ip >= ipPair.getKey() && ip <= ipPair.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Boolean isForeign(String ip) {
        return isForeign(ToolUtils.IP2long(ip));
    }

    /**
     * 内外网ip检测
     *
     * @param ip 待检测ip
     * @return true:国内ip false:境外ip
     */
    public boolean isDomestic(Long ip) {
        for (Pair<Long, Long> ipPair : this.ChinaIps) {
            if (ip >= ipPair.getKey() && ip <= ipPair.getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDomestic(String ip) {
        return isDomestic(ToolUtils.IP2long(ip));
    }


    /**
     * 初始化类
     * ip地址库地址参考github:[https://github.com/17mon/china_ip_list]
     */

    @Override
    public void init() {
        List<String> domesticIp =  FileUtils.readLine(NodeInfo.getConfig() + "/geo2ip/ChinaIPList.db");
        for (String ip : domesticIp) {
            this.ChinaIps.add(NetUtil.getRange(ip));
        }
        if(this.ChinaIps.isEmpty()){
            log.error("ip加载失败");
            throw new InitException("IP加载失败");
        }else{
            log.info("加载了{}IP",ChinaIps.size());
        }

    }
}
