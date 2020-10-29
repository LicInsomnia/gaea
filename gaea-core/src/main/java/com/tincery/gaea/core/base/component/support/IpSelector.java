package com.tincery.gaea.core.base.component.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.ConnectionTypeResponse;
import com.maxmind.geoip2.model.DomainResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.NetUtil;
import com.tincery.starter.base.InitializationRequired;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * 通过IP 可获取IP对应信息
 **/
@Slf4j
@Component
public class IpSelector implements InitializationRequired {

    private final Map<String, Location> bufferIps = new HashMap<>();
    private final Map<Pair<Long, Long>, String> reservedIps = new HashMap<>();
    private DatabaseReader regionReader = null;
    private DatabaseReader connectionReader = null;
    private DatabaseReader domainReader = null;
    private DatabaseReader ispReader = null;
    private Location dfLocation = new Location();
    @Autowired
    private CloudServerIpSelector cloudServerIpSelector;

    public static String location2Csv(Map<String, Object> location, char separator) {
        if (null == location || location.isEmpty()) {
            return "" + separator + separator + separator + separator + separator;
        }
        String country = location.getOrDefault("country", "-").toString();
        String region = location.getOrDefault("region", "-").toString();
        String city = location.getOrDefault("city", "-").toString();
        String lng = location.getOrDefault("lng", "0.0").toString();
        String lat = location.getOrDefault("lat", "0.0").toString();
        String cloudService = location.getOrDefault("cloud_service", "").toString();
        return country + separator +
                region + separator +
                city + separator +
                lng + separator +
                lat + separator +
                cloudService;
    }

    @Override
    public void init() {
        String path = NodeInfo.getConfig() + "/geo2ip/";
        this.dfLocation = new JSONObject((Map) CommonConfig.get("dflocation")).toJavaObject(Location.class);
        File regionDb = new File(path + "/GeoIP2-City.mmdb");
        File connectionDb = new File(path + "/GeoIP2-Connection-Type.mmdb");
        File domainDb = new File(path + "/GeoIP2-Domain.mmdb");
        File ispDb = new File(path + "/GeoIP2-ISP.mmdb");
        try {
            this.regionReader = new DatabaseReader.Builder(regionDb).build();
            this.connectionReader = new DatabaseReader.Builder(connectionDb).build();
            this.domainReader = new DatabaseReader.Builder(domainDb).build();
            this.ispReader = new DatabaseReader.Builder(ispDb).build();
        } catch (IOException e) {
            throw new InitException(StrUtil.format("初始化mmdb失败[{}]", e.getMessage()));

        }
        List<String> reservedIp = FileUtils.readLine(path + "/ReservedIPList.db");
        for (String buffer : reservedIp) {
            String[] element = buffer.split(":");
            String ip = element[0];
            String description = element[1];
            this.reservedIps.put(NetUtil.getRange(ip), description);
        }
    }

    /**
     * 获取ip地理位置信息
     *
     * @param ip 待查询ip地址
     * @return Map contains value or new HashMap
     */
    public Location getRegion(String ip) throws IOException, GeoIp2Exception {
        Location locationResult = new Location();
        InetAddress ipAddress;
        ipAddress = InetAddress.getByName(ip);
        CityResponse response = this.regionReader.city(ipAddress);
        Map<String, String> country = response.getCountry().getNames();
        Map<String, String> region = response.getMostSpecificSubdivision().getNames();
        com.maxmind.geoip2.record.Location location = response.getLocation();
        Map<String, String> city = response.getCity().getNames();
        locationResult.setCountry(country.getOrDefault("en", "-"));
        locationResult.setCountry_zh(country.getOrDefault("zh-CN", "-"));
        locationResult.setRegion(region.getOrDefault("en", "-"));
        locationResult.setRegion_zh(region.getOrDefault("zh_CN", "-"));
        locationResult.setCity(city.getOrDefault("zh_CN", "-"));
        locationResult.setCity_zh(city.getOrDefault("zh_CN", "-"));
        locationResult.setLng(location.getLongitude() == null ? -1.0 : location.getLongitude());
        locationResult.setLat(location.getLatitude() == null ? -1.0 : location.getLatitude());
        return locationResult;
    }

    /**
     * 获取ip连接类型
     *
     * @param ip 待查询ip
     * @return key:connection_type value: The database identifies dial-up, cellular, cable/DSL, corporate connection speeds
     */
    private String getConnectionType(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            ConnectionTypeResponse response = this.connectionReader.connectionType(ipAddress);
            if (null != response.getConnectionType()) {
                return response.getConnectionType().toString();
            }
        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 获取ip域名信息
     *
     * @param ip 待查询ip地址
     * @return key:domain
     */
    private Map<String, Object> getDomain(String ip) {
        Map<String, Object> result = new HashMap<>();
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            DomainResponse response = this.domainReader.domain(ipAddress);
            if (null != response.getDomain()) {
                result.put("domain", response.getDomain());
            }
            if (result.isEmpty()) {
                return null;
            } else {
                return result;
            }
        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
    }

    /**
     * 获取ip运营商信息
     *
     * @param ip 待查询ip地址
     * @return key:isp;organization;autonomous_system_Organization
     */
    private void setIsp(String ip, Location location) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        IspResponse response = this.ispReader.isp(ipAddress);
        location.setIsp(response.getIsp());
        location.setOrganization(response.getOrganization());
        location.setAutonomousSystemOrganization(response.getAutonomousSystemOrganization());
    }

    /**
     * 获取ip类型（保留地址类型）
     *
     * @param ip_n 待查询10进制ip地址
     * @return ip地址类型字符串
     */
    private String getIpType(Long ip_n) {
        for (Pair<Long, Long> ipPair : this.reservedIps.keySet()) {
            if (ip_n >= ipPair.getKey() && ip_n <= ipPair.getValue()) {
                return this.reservedIps.get(ipPair);
            }
        }
        return "normal";
    }

    /**
     * 获取ip通用信息
     *
     * @param ip 待查询ip地址
     * @return 错误 null 正确 key:country;region;city;lng;lat;connection_type;isp;organization;autonomous_system_Organization
     */
    public Location getCommonInformation(String ip) {
        Location location;
        try {
            location = this.getRegion(ip);
        } catch (IOException | GeoIp2Exception e) {
            location = dfLocation;
        }
        String type = getIpType(ToolUtils.IP2long(ip));
        location.setType(type);
        if ("normal".equals(type)) {
            location.setConnectionType(getConnectionType(ip));
            try {
                this.setIsp(ip, location);
            } catch (IOException | GeoIp2Exception ignore) {
            }
        }
        location.setCloudService(this.cloudServerIpSelector.getCloudService(ip));
        this.bufferIps.put(ip, location);
        return location;
    }

}


