package com.tincery.gaea.source.wechat.execute;


import com.tincery.gaea.api.src.WeChatData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;


/**
 * @author gxz
 */

@Component
public class WeChatLineAnalysis implements SrcLineAnalysis<WeChatData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 0.timeStamp 1.protocol 2.serverMac 3.clientMac
     * 4.serverIp_n 5.clientIp_n 6.serverPort 7.clientPort
     * 8.source 9.runleName
     * 10.imsi 11.imei 12.msisdn
     * 13.outclientip 14.outserverip 15.outclientport 16.outserverport 17.outproto
     * 18.userid 19.serverid 20.ismac2outer
     * 21.wxnum
     * 22.Version 23.OsType
     */
    @Override
    public WeChatData pack(String line) {
        WeChatData weChatData = new WeChatData();

        String[] elements = StringUtils.FileLineSplit(line);

        weChatData.setCapTime(DateUtils.validateTime(Long.parseLong(elements[0])));

        this.srcLineSupport.set7Tuple(elements[2],
                elements[3],
                elements[4],
                elements[5],
                elements[6],
                elements[7],
                elements[1],
                // proName 赋默认值  如果匹配到了相关application 会替换掉proName
                HeadConst.PRONAME.WECHAT,
                weChatData
        );
        weChatData.setSource(elements[8]);
        this.srcLineSupport.setTargetName(elements[9], weChatData);
        this.srcLineSupport.setGroupName(weChatData);
        weChatData.setImsi(elements[10])
                .setImei(elements[11])
                .setMsisdn(elements[12]);

        srcLineSupport.set5TupleOuter(elements[13], elements[14], elements[15], elements[16], elements[17], weChatData);

        weChatData.setUserId(elements[18])
                .setServerId(elements[19]);
        weChatData.setMacOuter("1".equals(elements[20]));
        weChatData.setWxNum(elements[21])
                .setVersion(elements[22])
                .setOsType(elements[23]);
        return weChatData;
    }



}
