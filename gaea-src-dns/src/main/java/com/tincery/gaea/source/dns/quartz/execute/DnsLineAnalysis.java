package com.tincery.gaea.source.dns.quartz.execute;


import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
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
    public PayloadDetector payloadDetector;

    @Autowired
    private GroupGetter groupGetter;

    public DnsLineAnalysis() {
    }

    /****
     * 将一条记录包装成实体类
     * 0.serverMac          1.clientMac         2.serverIp_n            3.clientIp_n
     * 4.serverPort         5.clientPort        6.protocol              7.uppkt
     * 8.downpkt            9.upbyte            10.downbyte             11.source
     * 12.ruleName          13.capTime             14.imsi                 15.imei
     * 16.msisdn            17.outclientip      18.outserverip          19.outclientport
     * 20.outserverport     21.outproto         22.userid               23.serverid
     * 24.ismac2outer       25.direct(0/1)      26.domain / upPayload   27.cname / downPayload
     * 28.ipv4              29.ipv6
     */
    @Override
    public DnsData pack(String line) {
        DnsData dnsData = new DnsData();
        String[] elements = StringUtils.FileLineSplit(line);
        dnsData.setDataType(Integer.parseInt(elements[25]));
        dnsData.setSource(elements[11]);
        dnsData.setTargetName(elements[12]);
        dnsData.setGroupName(this.groupGetter.getGroupName(dnsData.getTargetName()));
        long capTimeN = Long.parseLong(elements[13]);
        dnsData.setDurationTime(Long.parseLong(elements[3]) - capTimeN);
        dnsData.setCapTime(DateUtils.validateTime(capTimeN));
        set5Tuple(elements, dnsData);
        setFlow(elements, dnsData);
        dnsData.setImsi(elements[14]);
        dnsData.setImei(elements[15]);
        dnsData.setMsisdn(elements[16]);
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
                setMalformedPayload(elements, dnsData);
                break;
        }
        return dnsData;
    }

    private void set5Tuple(String[] elements, DnsData dnsData) {
        dnsData.setProtocol(Integer.parseInt(elements[6]));
        dnsData.setClientMac(elements[0]);
        dnsData.setServerMac(elements[1]);
        dnsData.setClientIp(NetworkUtil.arrangeIp(elements[2]));
        dnsData.setServerIp(NetworkUtil.arrangeIp(elements[3]));
        dnsData.setClientPort(Integer.parseInt(elements[4]));
        dnsData.setServerPort(Integer.parseInt(elements[5]));
        dnsData.setProName("DNS");
    }

    private void setFlow(String[] elements, DnsData dnsData) {
        dnsData.setUpPkt(Long.parseLong(elements[7]));
        dnsData.setUpByte(Long.parseLong(elements[9]));
        dnsData.setDownPkt(Long.parseLong(elements[8]));
        dnsData.setDownByte(Long.parseLong(elements[10]));
    }

    private void addresponseIp(String ip, DnsData dnsData) {
        Set<String> responseIp = dnsData.getResponseIp();
        if (null == responseIp) {
            responseIp = new HashSet<>();
        }
        responseIp.add(ip);
        dnsData.setResponseIp(responseIp);
    }

    private void setExtension(String extension, DnsData dnsData) {
        if (null == extension || extension.isEmpty()) {
            return;
        }
        Map<String, Object> extensionMap = new HashMap<>();
        String[] elements = extension.split(";");
        for (String element : elements) {
            String[] kvPair = element.split("=");
            if (kvPair.length != 2) {
                continue;
            }
            extensionMap.put(kvPair[0], kvPair[1]);
        }
        if (null != dnsData.getResponseIp()) {
            extensionMap.put(HeadConst.MONGO.IPS, dnsData.getResponseIp());
        }
        if (!extensionMap.isEmpty()) {
            dnsData.setExtension(extensionMap);
        }
    }

    private void setMalformedPayload(String[] elements, DnsData dnsData) {
        dnsData.setMalformedUpPayload("0000000000000000000000000000000000000000".equals(elements[26]) ? "" : elements[26]);
        dnsData.setMalformedDownPayload("0000000000000000000000000000000000000000".equals(elements[27]) ? "" : elements[27]);
        dnsData.setProName(payloadDetector.getProName(dnsData));
    }

}
