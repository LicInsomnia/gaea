package com.tincery.gaea.source.wechat.execute;


import com.tincery.gaea.api.src.WeChatData;
import com.tincery.gaea.core.base.mgt.HeadConst;
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
public class WeChatLineAnalysis implements SrcLineAnalysis<WeChatData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 0.syn/synack 1.fin
     * Time 2.startTime 3.endTime
     * flow 4.uppkt(c2s) 5.upbyte(c2s) 6.downpkt(s2c) 7.downbyte(s2c)
     * 7元组 8.protocol 9.smac 10.cMac 11.sip_n 12.cip_n 13.sport 14.cport
     * 15.source 16.runleName
     * 手机 17.imsi 18.imei 19.msisdn
     * 五元组 20.outclientip 21.outserverip 22.outclientport 23.outserverport 24.outproto
     * 25.userid 26.serverid 27.ismac2outer
     * 28.wxnum 29.Version 30.OsType
     */
    @Override
    public WeChatData pack(String line) {
        WeChatData weChatData = new WeChatData();
        String[] elements = StringUtils.FileLineSplit(line);
        fixCommon(weChatData,elements);
        fixOther(weChatData,elements);
        return weChatData;
    }

    /**
     * 设置wechat的本身属性
     * @param weChatData 实体
     * @param elements 数据源
     */
    private void fixOther(WeChatData weChatData, String[] elements) {
        weChatData.setWxNum(SourceFieldUtils.parseStringStrEmptyToNull(paramSplit(elements[28])))
                .setVersion(SourceFieldUtils.parseStringStrEmptyToNull(paramSplit(elements[29])))
                .setOsType(SourceFieldUtils.parseStringStrEmptyToNull(paramSplit(elements[30])));
    }

    /**
     * 设置common属性
     * @param weChatData 实体
     * @param elements 数据源
     */
    private void fixCommon(WeChatData weChatData, String[] elements) {
        this.srcLineSupport.setSynAndFin(elements[0],elements[1],weChatData);
        this.srcLineSupport.setTime(Long.parseLong(elements[2]),Long.parseLong(elements[3]),weChatData);
        this.srcLineSupport.setFlow(elements[4],elements[5],elements[6],elements[7],weChatData);
        this.srcLineSupport.set7Tuple(elements[9],
                elements[10],
                elements[11],
                elements[12],
                elements[13],
                elements[14],
                elements[8],
                // proName 赋默认值  如果匹配到了相关application 会替换掉proName
                HeadConst.PRONAME.WECHAT,
                weChatData
        );
        weChatData.setSource(SourceFieldUtils.parseStringStrEmptyToNull(elements[15]));
        this.srcLineSupport.setTargetName(elements[16], weChatData);
        this.srcLineSupport.setGroupName(weChatData);
        this.srcLineSupport.setMobileElements(elements[17],elements[18],elements[19],weChatData);
        this.srcLineSupport.set5TupleOuter(elements[20], elements[21], elements[22], elements[23], elements[24], weChatData);
        this.srcLineSupport.setPartiesId(elements[25],elements[26],weChatData);
        this.srcLineSupport.setIsMac2Outer(elements[27],weChatData);
    }

    public String paramSplit(String param){
        if (!StringUtils.isEmpty(param)) {
            if (param.contains(":")) {
                String[] split = param.split(":");
                if (split.length==2){
                    return split[1];
                }
            }
        }
        return null;
    }



}
