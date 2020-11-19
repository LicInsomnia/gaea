package com.tincery.gaea.source.ftpandtelnet.execute;


import com.tincery.gaea.api.src.FtpandtelnetData;
import com.tincery.gaea.api.src.extension.FtpAndTelnetExtension;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.gaea.source.ftpandtelnet.constant.FtpandtelnetConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public class FtpandtelnetLineAnalysis implements SrcLineAnalysis<FtpandtelnetData> {


    @Autowired
    private SrcLineSupport srcLineSupport;
    /**
     * 0.contentLength（4字节）|如果是malformed 为0  //这里用datatype判断
     *
     * 0.syn 1.fin 2.startTime 3.endTime 4.uppkt
     * 5.upbyte 6.downpkt 7.downbyte
     * 8.datatype(0。FTP/1.TELNET/-1malformed)
     * 9.protocol 10.serverMac 11.clientMac 12.serverIp_n
     * 13.clientIp_n 14.serverPort 15.clientPort 16.source
     * 17.runleName 18.imsi 19.imei 20.msisdn 21.outclientip
     * 22.outserverip 23.outclientport 24.outserverport 25.outproto
     * 26.userid 27.serverid 28.ismac2outer
     * -------------------------以上是全有的属性 以下是datatype！=-1的属性-------------------------------------
     * 29.username 30.password
     * 31.conflg 32.sucflg 33.content
     * --------------------------------以下是datetype = -1 的属性-------------------------------------
     * 29.upPayload 30.downPayload
     * @param line
     * @return
     */

    @Override
    public FtpandtelnetData pack(String line) {
        FtpandtelnetData ftpandtelnetData = new FtpandtelnetData();
        FtpAndTelnetExtension ftpAndTelnetExtension = new FtpAndTelnetExtension();
        String[] split = line.split(FtpandtelnetConstant.FTPANDTELNET_CONSTANT);
        String common =  split[0];
//        String index = split[1];
        String[] elements = StringUtils.FileLineSplit(common);
        fixCommon(elements,ftpandtelnetData);
        if (-1 == ftpandtelnetData.getDataType()){
            fixMalformed(elements, ftpandtelnetData);
        }else{
            if (0 == ftpandtelnetData.getDataType()){
                ftpandtelnetData.setProName("FTP");
            }else{
                ftpandtelnetData.setProName("TELNET");
            }
            fixFtpAndTelnet(elements,ftpAndTelnetExtension);
            String content = split[2];
            ftpAndTelnetExtension.setContent(content.replaceAll("\r\n","<br/>"));
        }
        ftpandtelnetData.setFtpAndTelnetExtension(ftpAndTelnetExtension);
        return ftpandtelnetData;
    }

    /**
     * 设置Malformed
     * @param elements
     * @param ftpandtelnetData
     */
    private void fixMalformed(String[] elements,FtpandtelnetData ftpandtelnetData){
        String replaceAll = elements[30].replaceAll("\u0000", "");
        srcLineSupport.setMalformedPayload(elements[29],replaceAll,ftpandtelnetData);
    }

    /**
     * datatype！=-1  设置ftpAndTelnetExtension
     * @param elements
     * @param ftpAndTelnetExtension
     */
    private void fixFtpAndTelnet(String[] elements,FtpAndTelnetExtension ftpAndTelnetExtension){
        ftpAndTelnetExtension.setUserName(elements[29])
                .setPassword(elements[30])
                .setConflg(elements[31]);
        String replace = elements[32].replaceAll("\u0000", "");
                ftpAndTelnetExtension.setSucflg(replace);

    }

    /**
     * 设置common属性  字段序列 0-29
     * @param elements
     * @param ftpandtelnetData
     */
    private void fixCommon(String[] elements,FtpandtelnetData ftpandtelnetData){
        ftpandtelnetData.setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]));
        srcLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), ftpandtelnetData);
        srcLineSupport.setFlow(elements[4], elements[5], elements[6], elements[7], ftpandtelnetData);
        ftpandtelnetData.setDataType(Integer.parseInt(elements[8]));
        srcLineSupport.set7Tuple(elements[10],
                elements[11],
                elements[12],
                elements[13],
                elements[14],
                elements[15],
                elements[9],
                "ftpandtelnet",
                ftpandtelnetData);
        ftpandtelnetData.setSource(elements[16]);
        srcLineSupport.setTargetName(elements[17], ftpandtelnetData);
        srcLineSupport.setGroupName(ftpandtelnetData);
        ftpandtelnetData.setImsi(elements[18])
                .setImei(elements[19])
                .setMsisdn(elements[20]);
        srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], ftpandtelnetData);
        ftpandtelnetData.setUserId(elements[26])
                .setServerId(elements[27]);
        ftpandtelnetData.setMacOuter("1".equals(elements[28]));
        ftpandtelnetData.setForeign(srcLineSupport.isForeign(ftpandtelnetData.getServerIp()));
    }


}
