package com.tincery.gaea.source.dns.quartz.execute;


import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author gxz
 */
@Component
public class DnsLineAnalysis implements SrcLineAnalysis<DnsData> {

    @Autowired
    private SrcLineSupport srcLineSupport;

    /**
     * 将一条记录包装成实体类
     * 0.serverMac          1.clientMac         2.serverIp_n            3.clientIp_n
     * 4.serverPort         5.clientPort        6.protocol              7.uppkt
     * 8.downpkt            9.upbyte            10.downbyte             11.source
     * 12.ruleName          13.capTime          14.imsi                 15.imei
     * 16.msisdn            17.outclientip      18.outserverip          19.outclientport
     * 20.outserverport     21.outproto         22.userid               23.serverid
     * 24.ismac2outer       25.direct(0/1)      26.domain / upPayload   27.cname / downPayload
     * 28.ipv4              29.ipv6
     */
    @Override
    public DnsData pack(String line) {
        DnsData dnsData = new DnsData();
        String[] elements = StringUtils.FileLineSplit(line);
        dnsData.setDataType(Integer.parseInt(elements[25]))
                .setSource(elements[11])
                .setDuration(0)
                .setCapTime(DateUtils.validateTime(Long.parseLong(elements[13])))
                .setSyn(false)
                .setFin(false)
                .setImsi(elements[14])
                .setImei(elements[15])
                .setMsisdn(elements[16]);
        this.srcLineSupport.setTargetName(elements[12], dnsData);
        this.srcLineSupport.setGroupName(dnsData);
        this.srcLineSupport.set7Tuple(elements[0], elements[1], elements[2], elements[3], elements[4], elements[5], elements[6], "DNS", dnsData);
        this.srcLineSupport.setFlow(elements[7], elements[9], elements[8], elements[10], dnsData);
        switch (dnsData.getDataType()) {
            case 0:
                dnsData.setDomain(elements[26]);
                break;
            case 1:
                dnsData.setDomain(elements[26].toLowerCase());
                String[] ipv4s = elements[28].split(";");
                String[] ipv6s = elements[29].split(";");
                for (String ipv4 : ipv4s) {
                    addresponseIp(ipv4, dnsData);
                }
                for (String ipv6 : ipv6s) {
                    addresponseIp(NetworkUtil.iPv6Hex2Host(ipv6), dnsData);
                }
                setExtension(elements[27], dnsData);
                break;
            default:
                this.srcLineSupport.setMalformedPayload(elements[26], elements[27], dnsData);
                break;
        }
        return dnsData;
    }

    private void addresponseIp(String ip, DnsData dnsData) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        Set<String> responseIp = dnsData.getResponseIp();
        if (null == responseIp) {
            responseIp = new HashSet<>();
        }
        responseIp.add(ip);
        dnsData.setResponseIp(responseIp);
    }

    private void setExtension(String extension, DnsData dnsData) {
        if (StringUtils.isEmpty(extension)) {
            return;
        }
        Map<String, Object> extensionMap = new HashMap<>();
        Set<String> cname = new HashSet<>();
        String[] elements = extension.split(";");
        for (String element : elements) {
            String[] kvPair = element.split("=");
            if (kvPair.length != 2) {
                continue;
            }
            if ("cname".equals(kvPair[0])) {
                cname.add(kvPair[1]);
                continue;
            }
            extensionMap.put(kvPair[0], kvPair[1]);
        }
        if (!cname.isEmpty()) {
            dnsData.setCname(cname);
        }
        if (!extensionMap.isEmpty()) {
            dnsData.setExtension(extensionMap);
        }
    }

}
