package com.tincery.gaea.source.dns.quartz.execute;


import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gxz
 */

@Component
public class DnsLineAnalysis implements SrcLineAnalysis<DnsData> {


    private final ApplicationProtocol applicationProtocol;

    @Autowired
    public PayloadDetector payloadDetector;

    @Autowired
    private GroupGetter groupGetter;


    public DnsLineAnalysis(ApplicationProtocol applicationProtocol) {
        this.applicationProtocol = applicationProtocol;
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
        dnsData.setProtocol(Integer.parseInt(elements[6]));
        dnsData.setClientMac(elements[0]);
        dnsData.setServerMac(elements[1]);
        dnsData.setClientIp(NetworkUtil.arrangeIp(elements[2]));
        dnsData.setServerIp(NetworkUtil.arrangeIp(elements[3]));
        dnsData.setClientPort(Integer.parseInt(elements[4]));
        dnsData.setServerPort(Integer.parseInt(elements[5]));
        dnsData.setProName("DNS");
        dnsData.setUpPkt(Long.parseLong(elements[7]));
        dnsData.setUpByte(Long.parseLong(elements[9]));
        dnsData.setDownPkt(Long.parseLong(elements[8]));
        dnsData.setDownByte(Long.parseLong(elements[10]));
        dnsData.setImsi(elements[14]);
        dnsData.setImei(elements[15]);
        dnsData.setMsisdn(elements[16]);
        switch (dnsData.getDataType()) {
            case 0:
                dnsData.setDomain(elements[26]);
                break;
            case 1:
                dnsData.setDomain(elements[26]);
//                dnsData.setExtension(elements[27]);
                String[] ipv4s = elements[28].split(";");
                String[] ipv6s = elements[29].split(";");
//                for (String ipv4 : ipv4s) {
//                    dnsData.addresponseIp(ipv4);
//                }
//                for (String ipv6 : ipv6s) {
//                    dnsData.addresponseIp(ToolUtils.IPv6Hex2Host(ipv6));
//                }
                break;
            default:
//                dnsUtils.setMalformedPayload(element[26], element[27], payloadDetector);
                break;
        }
//        dnsUtils.setOuter5Tuple(element[17], element[18], element[19], element[20], element[21]);
//        dnsUtils.setUserId(element[22]);
//        dnsUtils.setServerId(element[23]);
//        dnsUtils.setOuterFromMac(element[24]);
//        dnsUtils.reMarkTargetName(userId2TargetName);
//        dnsUtils.checkIsForeign(ipCheckUtils);
        return dnsData;
    }



}
