package com.tincery.gaea.source.snmp.execute;


import com.tincery.gaea.api.src.SnmpData;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
     *-----------------dataType = 1--------------------
     * 29.version	30.community	31.pduType
     * ----------------dataType = -1-------------------
     * 29.d2sPayload 30.s2dPayload
     */
    @Override
    public SnmpData pack(String line) {
        SnmpData snmpData = new SnmpData();
        String[] elements = StringUtils.FileLineSplit(line);
        fixCommon(snmpData,elements);
        Integer dataType = snmpData.getDataType();
        if (dataType == 1){
            fixNormal(snmpData,elements);
        }else if (dataType == -1){
            fixMalformed(snmpData,elements);
        }
        return snmpData;
    }

    /**
     * 设置malformed属性
     * @param snmpData 实体
     * @param elements 源
     */
    private void fixMalformed(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setMalformedPayload(elements[29],elements[30],snmpData);
    }

    /**
     * 装载标准数据属性
     * @param snmpData 实体
     * @param elements 源
     */
    private void fixNormal(SnmpData snmpData, String[] elements) {
        snmpData.setVersion(elements[29])
                .setCommunity(elements[30])
                .setPduType(elements[31]);
    }

    /**
     * TODO  因为这里暂时只有dataType  没看到文档中有s2dFlag  暂时无法区分客户端服务单
     * @param snmpData 数据实体
     * @param elements 数据源
     */
    private void fixCommon(SnmpData snmpData, String[] elements) {
        this.srcLineSupport.setSynAndFin(elements[0],elements[1],snmpData);
        this.srcLineSupport.setTime(elements[2],elements[3],snmpData);
        this.srcLineSupport.setFlow(elements[4],elements[5],elements[6],elements[7],snmpData);
        snmpData.setDataType(Integer.parseInt(elements[8]));
        this.srcLineSupport.set7Tuple(elements[10],elements[11],elements[12],elements[13],
                elements[14],elements[15],elements[9],"SNMP",snmpData);
        snmpData.setSource(elements[16]);
        this.srcLineSupport.setTargetName(elements[17],snmpData);
        this.srcLineSupport.setGroupName(snmpData);
        this.srcLineSupport.setMobileElements(elements[18],elements[19],elements[20],snmpData);
        this.srcLineSupport.set5TupleOuter(elements[21],elements[22],elements[23],elements[24],elements[25],snmpData);
        this.srcLineSupport.setPartiesId(elements[26],elements[27],snmpData);
        this.srcLineSupport.setIsMac2Outer(elements[28],snmpData);
    }
}
