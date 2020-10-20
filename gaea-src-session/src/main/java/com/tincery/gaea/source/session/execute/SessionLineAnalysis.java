package com.tincery.gaea.source.session.execute;


import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gxz
 */

@Component
public class SessionLineAnalysis implements SrcLineAnalysis<SessionData> {

    @Autowired
    private SrcLineSupport srcLineSupport;

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
        if (!tryGetServerProName(elements, sessionMetaData)) {
            tryGetClientProName(elements, sessionMetaData);
        }
        sessionMetaData.setDataType("other".equals(sessionMetaData.getProName()) ? 0 : 1);
        return sessionMetaData;
    }


    /****
     * 这是固定的属性  无论匹配到了server 还是client 都不会变的属性
     * @author gxz
     **/
    private void setFixProperties(String[] element, SessionData sessionData) {
        long capTimeN = Long.parseLong(element[2]);
        sessionData.setCapTime(DateUtils.validateTime(capTimeN))
                .setDurationTime(Long.parseLong(element[3]) - capTimeN)
                .setSource(element[15])
                .setImsi(SourceFieldUtils.parseStringStr(element[17]))
                .setImei(SourceFieldUtils.parseStringStr(element[18]))
                .setMsisdn(SourceFieldUtils.parseStringStr(element[19]))
                .setUserId(element[25])
                .setServerId(element[26])
                .setSyn(SourceFieldUtils.parseBooleanStr(element[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(element[1]));
        sessionData.setUpPayLoad(SourceFieldUtils.parseStringStr(element[28]))
                .setDownPayLoad(SourceFieldUtils.parseStringStr(element[29]))
                .setMacOuter(SourceFieldUtils.parseBooleanStr(element[27]));
        this.srcLineSupport.set5TupleOuter(
                element[20],
                element[21],
                element[22],
                element[23],
                element[24],
                sessionData
        );
    }

    /****
     * 通过protocol + serverport 尝试获取内容
     * @author gxz
     * @param element element
     * @param sessionData 组织起来的数据
     * @return boolean 是否找到
     **/
    private boolean tryGetServerProName(String[] element, SessionData sessionData) {
        this.srcLineSupport.set7Tuple(
                element[9],
                element[10],
                element[11],
                element[12],
                element[13],
                element[14],
                element[8],
                "other",
                sessionData);
        this.srcLineSupport.setFlow(element[4], element[5], element[6], element[7], sessionData);
        String serverKey = element[8] + "_" + element[13];
        return getApplicationInformation(sessionData, serverKey);
    }

    private boolean tryGetClientProName(String[] element, SessionData sessionData) {
        this.srcLineSupport.set7Tuple(
                element[10],
                element[9],
                element[12],
                element[11],
                element[14],
                element[13],
                element[8],
                "other",
                sessionData
        );
        this.srcLineSupport.setFlow(element[6], element[7], element[4], element[5], sessionData);
        String clientKey = element[8] + "_" + element[14];
        return getApplicationInformation(sessionData, clientKey);
    }

    private boolean getApplicationInformation(SessionData sessionData, String key) {
        ApplicationInformationBO clientApplication = this.srcLineSupport.getApplication(key);
        if (clientApplication == null) {
            return false;
        }
        String proName = clientApplication.getProName();
        if (StringUtils.isNotEmpty(proName)) {
            sessionData.setProName(proName);
        }
        return true;
    }


}
