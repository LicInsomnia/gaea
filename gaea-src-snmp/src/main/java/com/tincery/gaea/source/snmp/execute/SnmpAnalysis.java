package com.tincery.gaea.source.snmp.execute;


import com.tincery.gaea.api.src.SnmpData;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * @author gxz
 */

@Component
public class SnmpAnalysis implements SrcLineAnalysis<SnmpData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 封装到来的Snmp数据
     * 0.syn 1.fin
     * Time: 2.startTime 3.endTime
     * Flow: 4.d2spkt 5.d2sbyte 6.s2dpkt 7.s2dbyte
     * 8.datatype(1/-1)
     * 七元组：9.protocol 10.srcMac 11.dstMac 12.srcIp_n 13.dstIp_n 14.srcPort 15.dstIPort
     * 16.source 17.runleName
     * 手机：18.imsi 19.imei 20.msisdn
     * 五元组：21.outdstip 22.outsrcip 23.outdstport 24.outsrcport 25.outproto
     * 26.dstid 27.srcrid 28.ismac2outer
     * -----------------dataType = 1--------------------
     * 29.version	30.community	31.s2dFlag:pduType
     * ----------------dataType = -1-------------------
     * 29.d2sPayload 30.s2dPayload
     */
    @Override
    public SnmpData pack(String line) {
        SnmpData snmpData = new SnmpData();
        String[] elements = StringUtils.FileLineSplit(line);
        boolean s2dFlag = fixDataType(snmpData, elements);
        fixCommon(snmpData,elements);
        if (s2dFlag) {
            fixCommonForward(snmpData, elements);
        } else {
            fixCommonReverse(snmpData, elements);
        }
        return snmpData;
    }

    /**
     * 反向装填
     *
     * @param snmpData 要装填的实体
     * @param elements 数据源
     */
    private void fixCommonReverse(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setFlow(elements[6], elements[7], elements[4], elements[5], snmpData);
        this.srcLineSupport.set7Tuple(elements[11], elements[10], elements[13], elements[12],
                elements[15], elements[14], elements[9], "SNMP", snmpData);
        this.srcLineSupport.set5TupleOuter(elements[22], elements[21], elements[24], elements[23], elements[25], snmpData);
    }

    /**
     * 装载dataType  并且返回s2dFlag
     *
     * @param snmpData 要装填的实体
     * @param elements 数据源
     * @return boolean  如果返回是true  直接正向装填  如果是false  反向装填
     */
    private boolean fixDataType(SnmpData snmpData, String[] elements) {
        int dataType = Integer.parseInt(elements[8]);
        snmpData.setDataType(dataType);
        /* malformed 正向装填 */
        if (dataType == -1) {
            fixMalformed(snmpData, elements);
            return true;
        } else if (dataType == 1) {
            String[] temp = elements[31].split(":");
            String flag = temp[0];
            snmpData.setPduType(temp[1]);
            fixNormal(snmpData, elements);
            if (Objects.equals(flag, "D2S")) {
                return true;
            } else {
                return !Objects.equals(flag, "S2D");
            }
        }
        return true;
    }

    /**
     * 设置malformed属性
     *
     * @param snmpData 实体
     * @param elements 源
     */
    private void fixMalformed(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setMalformedPayload(elements[29], elements[30], snmpData);
    }

    /**
     * 装载标准数据属性
     *
     * @param snmpData 实体
     * @param elements 源
     */
    private void fixNormal(SnmpData snmpData, String[] elements) {
        snmpData.setVersion(elements[29])
                .setCommunity(elements[30]);
    }

    /**
     * @param snmpData 数据实体
     * @param elements 数据源
     */
    private void fixCommon(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setSynAndFin(elements[0], elements[1], snmpData);
        this.srcLineSupport.setTime(elements[2], elements[3], snmpData);
        snmpData.setSource(elements[16]);
        this.srcLineSupport.setTargetName(elements[17], snmpData);
        this.srcLineSupport.setGroupName(snmpData);
        this.srcLineSupport.setMobileElements(elements[18], elements[19], elements[20], snmpData);
        this.srcLineSupport.setPartiesId(elements[26], elements[27], snmpData);
        this.srcLineSupport.setIsMac2Outer(elements[28], snmpData);
    }

    /**
     * 装载正向的数据
     * @param snmpData 要装载的实体
     * @param elements 数据源
     */
    private void fixCommonForward(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setFlow(elements[4], elements[5], elements[6], elements[7], snmpData);
        this.srcLineSupport.set7Tuple(elements[10], elements[11], elements[12], elements[13],
                elements[14], elements[15], elements[9], "SNMP", snmpData);
        this.srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], snmpData);

    }
}
