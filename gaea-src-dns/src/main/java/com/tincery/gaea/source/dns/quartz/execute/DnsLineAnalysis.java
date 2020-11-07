package com.tincery.gaea.source.dns.quartz.execute;


import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.api.src.extension.DnsExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
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
    /**
     * 0. srcMac 1.dstMac 2.srcIp_n 3.dstIp_n 4.srcPort 5.dstPort 6.protocol
     * 7.d2spkt 8.s2dpkt 9.d2sbyte 10.s2dbyte 11.source 12.ruleName 13.time
     * 14.imsi 15.imei 16.msisdn 17.outdsttip 18.outsrcip 19.outdstport
     * 20.outsrcport 21.outproto 22.dstid 23.srcid 24.ismac2outer
     * -------------------------------以下顺序有变化--------------------------------------
     * 25.s2dflag(1/2)* 1:s2d数据 2:d2s数据
     * 26.datatype(0/1) 0:请求数据 1:回应数据  注：当s2dflag == 1 并且datatype == 1时srcIp为服务器Ip
     * 当s2dflag == 2  并且datatype == 0时srcIp为服务器Ip
     * 其余情况 时srcIp为客户端Ip
     * 27.domain 28.cname 29.ipv4 30.ipv6
     */

    @Override
    public DnsData pack(String line) {
        DnsData dnsData = new DnsData();
        String[] elements = StringUtils.FileLineSplit(line);

        dnsData.setSource(elements[11])
                .setDuration(0L)
                .setCapTime(DateUtils.validateTime(Long.parseLong(elements[13])))
                .setSyn(false)
                .setFin(false);
        this.srcLineSupport.setTargetName(elements[12], dnsData);
        this.srcLineSupport.setGroupName(dnsData);

        Integer s2dFlag = Integer.parseInt(elements[25]);
        Integer dataType = Integer.parseInt(elements[26]);
        dnsData.setDataType(dataType);
        /*
        根据s2dFlag 和dataType判断  装载顺序
                    s2d             1                         2
        dataType 0:        s:client d:server         s:server d:client
        dataType 1:        s:server d:client         s:client d:server
         */
        switch (dataType){
            case 0:
                if (Objects.equals(s2dFlag,1)){
                    //s: client  d:server d2sbyte:down s2dbyte:up
                    fixS2D(elements,dnsData);
                }else if (Objects.equals(s2dFlag,2)){
                    //s: server d:client d2sbyte: up  s2dbyte:down
                    fixD2S(elements,dnsData);
                }
                break;
            case 1:
                if (Objects.equals(s2dFlag,1)){
                    fixD2S(elements,dnsData);
                }else if (Objects.equals(s2dFlag,2)){
                    fixS2D(elements,dnsData);
                }
                break;
            case -1:
                /*
                s2dport<= d2sport fixD2S(elements,dnsData);
                s2dport > d2sport fixS2D(elements,dnsData);
                 */
                int srcPort = Integer.parseInt(elements[4]);
                int dstPort = Integer.parseInt(elements[5]);
                if (srcPort<=dstPort){
                    fixD2S(elements,dnsData);
                }else{
                    fixS2D(elements,dnsData);
                }
                break;
        }
        this.srcLineSupport.setMobileElements(elements[14], elements[15], elements[16], dnsData);
        this.srcLineSupport.setPartiesId(elements[22], elements[23], dnsData);
        dnsData.setMacOuter("1".equals(elements[24]));

        DnsExtension dnsExtension = packExtension(elements, dnsData);
        dnsData.setDnsExtension(dnsExtension);
        return dnsData;
    }
    /*根据s2dFLag 和datatype 处理   s2dFlag 1 dataType 0   s2dFlag 2 dataType 1*/
    private void fixS2D(String[] elements,DnsData dnsData){
        this.srcLineSupport.set7Tuple(
                elements[1],
                elements[0],
                elements[3],
                elements[2],
                elements[5],
                elements[4],
                elements[6],
                HeadConst.PRONAME.DNS,
                dnsData
        );
        //upPkt,
        //String upByte,
        //String downPkt,
        //tring downByte,
        this.srcLineSupport.setFlow(
                elements[8],
                elements[10],
                elements[7],
                elements[9],
                dnsData
        );
    }
    /*根据s2dFLag 和datatype 处理 s2dFlag 2 dataType 0   s2dFlag 1 dataType 1*/
    private void fixD2S(String[] elements,DnsData dnsData){
        /*
          String serverMac,
          String clientMac,
          String serverIp,
          String clientIp,
          String serverPort,
          String clientPort,
          String protocol,
         */
        this.srcLineSupport.set7Tuple(
                elements[0],
                elements[1],
                elements[2],
                elements[3],
                elements[4],
                elements[5],
                elements[6],
                HeadConst.PRONAME.DNS,
                dnsData
        );
        //upPkt,
        //String upByte,
        //String downPkt,
        //tring downByte,
        this.srcLineSupport.setFlow(
                elements[7],
                elements[9],
                elements[8],
                elements[10],
                dnsData
        );
    }

    private DnsExtension packExtension(String[] elements, DnsData dnsData) {
        int dataType = dnsData.getDataType();
        DnsExtension dnsExtension = new DnsExtension();
        switch (dataType) {
            case 0:
                dnsExtension.setDomain(elements[27].toLowerCase());
                break;
            case 1:
                dnsExtension.setDomain(elements[27].toLowerCase());
                String[] ipv4s = elements[29].split(";");
                String[] ipv6s = elements[30].split(";");
                for (String ipv4 : ipv4s) {
                    addresponseIp(ipv4, dnsExtension);
                }
                for (String ipv6 : ipv6s) {
                    addresponseIp(NetworkUtil.iPv6Hex2Host(ipv6), dnsExtension);
                }
                setExtension(elements[28], dnsExtension);
                break;
            default:
                this.srcLineSupport.setMalformedPayload(elements[27], elements[28], dnsData);
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
