package com.tincery.gaea.source.email.execute;


import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.NumberUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gxz
 */

@Component
public class SessionLineAnalysis implements SrcLineAnalysis<SessionData> {


    private final ApplicationProtocol applicationProtocol;

    @Autowired
    public PayloadDetector payloadDetector;


    public SessionLineAnalysis(ApplicationProtocol applicationProtocol) {
        this.applicationProtocol = applicationProtocol;
    }

    /****
     * 将一条记录包装成实体类
     *
     * 0.syn/synack      1.fin             2.startTime      3.endTime
     * 4.uppkt(c2s)      5.upbyte(c2s)     6.downpkt(s2c    7.downbyte(s2c)
     * 8.protocol        9.smac            10.cMac          11.sip_n
     * 12.cip_n          13.sport          14.cport         15.source
     * 16.ruleName       17.imsi           18.imei          19.msisdn
     * 20.outclientip    21.outserverip    22.outclientport 23.outserverport
     * 24.outproto       25.userid         26.serverid      27.ismac2outer
     * 28.cpayload       29.spayload
     * 赋值固定属性------> 尝试通过protocol +serverPort 获取key 赋值 ----->
     * 如果没获取到 再尝试通过protocol+clientPort 获取key 赋值 --->
     * 获取与否的区别是   一些server clint 相关属性 在element的位置会变化
     * 如果获取到了client  将会转换顺序赋值
     * proName 是通过获取的application赋值
     */
    @Override
    public SessionData pack(String line) {
        SessionData sessionMetaData = new SessionData();
        String[] elements = StringUtils.FileLineSplit(line);
        setFixProperties(elements, sessionMetaData);
        // proName 赋默认值  如果匹配到了相关application 会替换掉proName
        sessionMetaData.setProName("other");
        if (tryGetServerProName(elements, sessionMetaData) || tryGetClientProName(elements, sessionMetaData)) {
        }
        return sessionMetaData;
    }


    /****
     * 这是固定的属性  无论匹配到了server 还是client 都不会变的属性
     * @author gxz
     **/
    private void setFixProperties(String[] element, SessionData sessionData) {
        long captimeN = Long.parseLong(element[2]);
        sessionData.setDurationTime(Long.parseLong(element[3]) - captimeN);
        sessionData.setImsi(element[17]);
        sessionData.setImei(element[18]);
        sessionData.setMsisdn(element[19]);
        sessionData.setCapTime(DateUtils.validateTime(captimeN));
        sessionData.setUpPayLoad(element[28]).setDownPayLoad(element[29])
                .setProtocol(Integer.parseInt(element[8]))
                .setClientIpOuter(element[20])
                .setServerIpOuter(element[21])
                .setClientPortOuter(NumberUtils.parseIntegerStr(element[22]))
                .setServerPortOuter(NumberUtils.parseIntegerStr(element[23]))
                .setProtocolOuter(NumberUtils.parseIntegerStr(element[24]))
                .setUserId(element[25]);
        sessionData.setServerId(element[26]);
        sessionData.setSyn("1".equals(element[0]));
        sessionData.setFin("1".equals(element[1]));
    }

    /****
     * 通过protocol + serverport 尝试获取内容
     * @author gxz
     * @param element element
     * @param sessionData 组织起来的数据
     * @return boolean 是否找到
     **/
    private boolean tryGetServerProName(String[] element, SessionData sessionData) {
        sessionData.setServerMac(element[9]).setClientMac(element[10])
                .setServerIp(NetworkUtil.arrangeIp(element[11]))
                .setClientIp(NetworkUtil.arrangeIp(element[12]))
                .setServerPort(Integer.parseInt(element[13]))
                .setClientPort(Integer.parseInt(element[14]))
                .setUpPkt(Long.parseLong(element[4]))
                .setUpByte(Long.parseLong(element[5]))
                .setDownPkt(Long.parseLong(element[6]))
                .setDownByte(Long.parseLong(element[7]));
        String serverKey = element[8] + "_" + element[13];
        ApplicationInformationBO serverApplication = applicationProtocol.getApplication(serverKey);
        if (serverApplication == null) {
            return false;
        }
        sessionData.setProName(serverApplication.getProName());
        return true;
    }

    private boolean tryGetClientProName(String[] element, SessionData sessionData) {
        String clientKey = element[8] + "_" + element[14];
        ApplicationInformationBO clientApplication = applicationProtocol.getApplication(clientKey);
        if (clientApplication == null) {
            return false;
        }
        sessionData.setServerMac(element[10]).setClientMac(element[9])
                .setServerIp(NetworkUtil.arrangeIp(element[12]))
                .setClientIp(NetworkUtil.arrangeIp(element[11]))
                .setServerPort(Integer.parseInt(element[14]))
                .setClientPort(Integer.parseInt(element[13]))
                .setUpPkt(Long.parseLong(element[6]))
                .setUpByte(Long.parseLong(element[7]))
                .setDownPkt(Long.parseLong(element[4]))
                .setDownByte(Long.parseLong(element[5]))
                .setProName(clientApplication.getProName());
        return true;
    }


}
