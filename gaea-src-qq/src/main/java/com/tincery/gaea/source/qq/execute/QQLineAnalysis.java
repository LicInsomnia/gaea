package com.tincery.gaea.source.qq.execute;


import com.tincery.gaea.api.src.QQData;
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
public class QQLineAnalysis implements SrcLineAnalysis<QQData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 封装到来的qq数据
     * 0.syn/synack 1.fin 2.startTime 3.endTime 4.uppkt(c2s)
     * 5.upbyte(c2s) 6.downpkt(s2c) 7.downbyte(s2c) 8.protocol
     * 9.smac 10.cMac 11.sip_n 12.cip_n 13.sport 14.cport 15.source
     * 16.runleName 17.imsi 18.imei 19.msisdn 20.outclientip
     * 21.outserverip 22.outclientport 23.outserverport 24.outproto
     * 25.userid 26.serverid 27.ismac2outer 28.qq
     *
     */
    @Override
    public QQData pack(String line) {
        QQData qqData = new QQData();
        String[] elements = StringUtils.FileLineSplit(line);
        fixCommon(qqData,elements);
        fixOther(qqData,elements);
        return qqData;
    }

    /**
     * 设置common属性
     * @param qqData 实体
     * @param elements 数据源
     */
    private void fixCommon(QQData qqData,String[] elements){
        this.srcLineSupport.setSynAndFin(elements[0],elements[1],qqData);
        srcLineSupport.setTime(Long.parseLong(elements[2]),Long.parseLong(elements[3]),qqData);
        this.srcLineSupport.setFlow(elements[4],elements[5],elements[6],elements[7],qqData);
        this.srcLineSupport.set7Tuple(elements[9],
                elements[10],
                elements[11],
                elements[12],
                elements[13],
                elements[14],
                elements[8],
                // proName 赋默认值  如果匹配到了相关application 会替换掉proName
                HeadConst.PRONAME.QQ,
                qqData
        );
        qqData.setSource(SourceFieldUtils.parseStringStrEmptyToNull(elements[15]));
        this.srcLineSupport.setTargetName(elements[16], qqData);
        this.srcLineSupport.setGroupName(qqData);
        this.srcLineSupport.setMobileElements(elements[17],elements[18],elements[19],qqData);
        this.srcLineSupport.set5TupleOuter(elements[20], elements[21], elements[22], elements[23], elements[24], qqData);
        this.srcLineSupport.setPartiesId(elements[25],elements[26],qqData);
        this.srcLineSupport.setIsMac2Outer(elements[27],qqData);

    }

    /**
     * 装载除了Common的其他信息
     * @param qqData 数据实体
     * @param elements 一条数据
     */
    private void fixOther(QQData qqData,String[] elements){
        qqData.setQq(elements[28]);
    }
}
