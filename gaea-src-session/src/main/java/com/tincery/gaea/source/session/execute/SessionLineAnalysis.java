package com.tincery.gaea.source.session.execute;


import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.api.src.extension.SessionExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * @author gxz
 */

@Component
@Slf4j
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
        sessionMetaData.setDataType(HeadConst.PRONAME.OTHER.equals(sessionMetaData.getProName()) ? 0 : 1);
        return sessionMetaData;
    }

    /****
     * 这是固定的属性  无论匹配到了server 还是client 都不会变的属性
     * @author gxz
     **/
    private void setFixProperties(String[] elements, SessionData sessionData) {

    srcLineSupport.setTime(Long.parseLong(elements[2]),Long.parseLong(elements[3]),sessionData);
    sessionData.setSource(elements[15])
                .setImsi(SourceFieldUtils.parseStringStr(elements[17]))
                .setImei(SourceFieldUtils.parseStringStr(elements[18]))
                .setMsisdn(SourceFieldUtils.parseStringStr(elements[19]))
                .setUserId(elements[25])
                .setServerId(elements[26])
                .setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        sessionData.setMacOuter(SourceFieldUtils.parseBooleanStr(elements[27]));
        this.srcLineSupport.set5TupleOuter(
                elements[20],
                elements[21],
                elements[22],
                elements[23],
                elements[24],
                sessionData
        );
        SessionExtension sessionExtension = new SessionExtension();
        sessionExtension.setUpPayLoad(SourceFieldUtils.parseStringStr(elements[28]));
        sessionExtension.setDownPayLoad(SourceFieldUtils.parseStringStr(elements[29]));
        sessionData.setSessionExtension(sessionExtension);
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
                HeadConst.PRONAME.OTHER,
                sessionData);
        //设置境内外要在设置7元组之后,要不然没有serverIp
        try {
            sessionData.setForeign(this.srcLineSupport.isForeign(sessionData.getServerIp()));
        }catch (RuntimeException e){
            sessionData.setForeign(false);
            log.warn("无法解析内外网ipv6地址，数据为{}", Arrays.asList(element));
        }

        this.srcLineSupport.setFlow(element[4], element[5], element[6], element[7], sessionData);
        String serverKey = element[8] + "_" + element[13];
        return this.srcLineSupport.setProName(serverKey, sessionData);
    }

    private void tryGetClientProName(String[] element, SessionData sessionData) {
        String clientKey = element[8] + "_" + element[14];
        if (this.srcLineSupport.setProName(clientKey, sessionData)) {
            this.srcLineSupport.set7Tuple(
                    element[10],
                    element[9],
                    element[12],
                    element[11],
                    element[14],
                    element[13],
                    element[8],
                    HeadConst.PRONAME.OTHER,
                    sessionData
            );
            this.srcLineSupport.setFlow(element[6], element[7], element[4], element[5], sessionData);
            //设置境内外要在设置7元组之后,要不然没有serverIp
            sessionData.setForeign(this.srcLineSupport.isForeign(sessionData.getServerIp()));
        }
    }

}
