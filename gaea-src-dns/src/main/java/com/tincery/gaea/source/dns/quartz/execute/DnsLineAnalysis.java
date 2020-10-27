package com.tincery.gaea.source.dns.quartz.execute;


import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.api.src.extension.DnsExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
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
                .setCapTime(Long.parseLong(elements[13]))
                .setSyn(false)
                .setFin(false);
        this.srcLineSupport.setTargetName(elements[12], dnsData);
        this.srcLineSupport.setGroupName(dnsData);
        this.srcLineSupport.set7Tuple(elements[0],
                elements[1],
                elements[2],
                elements[3],
                elements[4],
                elements[5],
                elements[6],
                HeadConst.PRONAME.DNS,
                dnsData
        );
        this.srcLineSupport.setFlow(elements[7],
                elements[9],
                elements[8],
                elements[10],
                dnsData
        );
        this.srcLineSupport.setMobileElements(elements[14], elements[15], elements[16], dnsData);
        this.srcLineSupport.setPartiesId(elements[22], elements[23], dnsData);
        dnsData.setMacOuter("1".equals(elements[24]));
        DnsExtension dnsExtension = packExtension(elements, dnsData);
        dnsData.setDnsExtension(dnsExtension);
        return dnsData;
    }

    private DnsExtension packExtension(String[] elements, DnsData dnsData) {
        int dataType = dnsData.getDataType();
        DnsExtension dnsExtension = new DnsExtension();
        switch (dataType) {
            case 0:
                dnsExtension.setDomain(elements[26].toLowerCase());
                break;
            case 1:
                dnsExtension.setDomain(elements[26].toLowerCase());
                String[] ipv4s = elements[28].split(";");
                String[] ipv6s = elements[29].split(";");
                for (String ipv4 : ipv4s) {
                    addresponseIp(ipv4, dnsExtension);
                }
                for (String ipv6 : ipv6s) {
                    addresponseIp(NetworkUtil.iPv6Hex2Host(ipv6), dnsExtension);
                }
                setExtension(elements[27], dnsExtension);
                break;
            default:
                this.srcLineSupport.setMalformedPayload(elements[26], elements[27], dnsData);
                break;
        }
        return dnsExtension;
    }

    private void addresponseIp(String ip, DnsExtension dnsExtension) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        Set<String> responseIp = dnsExtension.getResponseIp();
        if (null == responseIp) {
            responseIp = new HashSet<>();
        }
        responseIp.add(ip);
        dnsExtension.setResponseIp(responseIp);
    }

    private void setExtension(String extension, DnsExtension dnsExtension) {
        if (StringUtils.isEmpty(extension)) {
            return;
        }
        Set<String> cname = new HashSet<>();
        String[] elements = extension.split(";");
        for (String element : elements) {
            String[] kvPair = element.split("=");
            if (kvPair.length != 2) {
                continue;
            }
            if ("cname".equals(kvPair[0])) {
                cname.add(kvPair[1]);
            }
        }
        if (!cname.isEmpty()) {
            dnsExtension.setCname(cname);
        }
    }

}
