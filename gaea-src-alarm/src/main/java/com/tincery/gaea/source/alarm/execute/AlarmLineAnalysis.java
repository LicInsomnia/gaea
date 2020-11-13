package com.tincery.gaea.source.alarm.execute;

import com.tincery.gaea.api.src.AlarmTupleData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gongxuanzhang
 */
@Component
public class AlarmLineAnalysis implements SrcLineAnalysis<AlarmTupleData> {

    @Autowired
    private SrcLineSupport srcLineSupport;


    /**
     * 解析探针出来的alarm txt
     * 0.timestamp 1.uppkt 2.upbyte 3.downpkt 4.downbyte
     * 5.prococol 6.smac 7.dmac 8.sip_n 9.dip_n 10.sport
     * 11.dport 12.source 13.rulename 14.imsi 15.imei
     * 16.msisdn 17.outsip_n 18.outdip_n 19.outsport
     * 20.outdport 21.outproto 22.userid 23.serverid
     * 24.ismac2outer
     **/
    @Override
    public AlarmTupleData pack(String line){
        AlarmTupleData alarmTupleData = new AlarmTupleData();
        String[] elements = StringUtils.FileLineSplit(line);
        /*1.判断数据装填方向*/
        boolean s2dFlag = judgeS2D(elements);
        /*2.通过装填方向装填数据*/
        fixCommonByS2DFlag(elements,alarmTupleData,s2dFlag);
        return alarmTupleData;
    }

    /**
     * 通过装填方向装填数据
     * @param s2dFlag true：{s：server|Down d:client|Up} false:{s:client|Up d:server|Down}
     */
    private void fixCommonByS2DFlag(String[] elements, AlarmTupleData alarmTupleData, boolean s2dFlag) {
        alarmTupleData.setCapTime(DateUtils.validateTime(Long.parseLong(elements[0])));
        alarmTupleData.setSource(elements[12]);
        srcLineSupport.setMobileElements(elements[14],elements[15],elements[16],alarmTupleData);
        if (s2dFlag){
            /*{s：server|Down d:client|Up}*/
            srcLineSupport.set5TupleOuter(elements[18],elements[17],elements[20],elements[19],elements[21],alarmTupleData);
            srcLineSupport.set7Tuple(elements[6],elements[7],elements[8],elements[9],elements[10],elements[11],elements[5],"other",alarmTupleData);
            srcLineSupport.setPartiesId(elements[22], elements[23], alarmTupleData);
            /*1.uppkt 2.upbyte 3.downpkt 4.downbyte*/
            /*  d2spkt s2dpkt d2sbyte s2dbyte*/
            this.srcLineSupport.setFlow(
                    elements[1],
                    elements[3],
                    elements[2],
                    elements[4],
                    alarmTupleData
            );
        }else {
            srcLineSupport.set5TupleOuter(elements[17],elements[18],elements[19],elements[20],elements[21],alarmTupleData);
            srcLineSupport.set7Tuple(elements[7],elements[6],elements[9],elements[8],elements[11],elements[10],elements[5],"other",alarmTupleData);
            srcLineSupport.setPartiesId(elements[23], elements[22], alarmTupleData);
            /*1.uppkt 2.upbyte 3.downpkt 4.downbyte*/
            /*  d2spkt s2dpkt d2sbyte s2dbyte*/
            this.srcLineSupport.setFlow(
                    elements[2],
                    elements[4],
                    elements[1],
                    elements[3],
                    alarmTupleData);
        }
        this.srcLineSupport.setTargetName(elements[13], alarmTupleData);
        this.srcLineSupport.setGroupName(alarmTupleData);
    }

    /**
     * @return S2D的方向  true s为server d为client | false s为client d为server
     */
    private boolean judgeS2D(String[] elements) {
        String sIp = elements[9];
        String dIp = elements[10];
        int sPort = Integer.parseInt(elements[11]);
        int dPort = Integer.parseInt(elements[12]);

        /*1.先通过内网外网ip判断
        *                                   Server  Client  Up      Down
        * srcip是内网地址，dstip是外网地址	dstXX	srcXX	s2dXX	d2sXX
        * srcip是外网地址，dstip是内网地址	srcXX	dstXX	d2sXX	s2dXX
        * */
        try {
            boolean innerSIp = srcLineSupport.isInnerIp(sIp);
            boolean innerDIp = srcLineSupport.isInnerIp(dIp);
            if (innerSIp && !innerDIp){
                return false;
            }else if (!innerSIp && innerDIp){
                return true;
            }
        }catch (Exception e){
            /*这里有可能有Ipv6的地址  所以捕获后进行端口判别*/
            return sPort <= dPort;
        }
        /*2.进行端口判别 srcPort<=dstPort
        *ServerXX	ClientXX	UpXX	DownXX
        * srcXX	        dstXX	d2sXX	s2dXX
        * */
        return sPort <= dPort;
    }


}
