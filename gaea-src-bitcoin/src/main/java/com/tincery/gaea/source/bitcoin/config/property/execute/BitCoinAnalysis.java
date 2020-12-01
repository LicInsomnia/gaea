package com.tincery.gaea.source.bitcoin.config.property.execute;


import com.tincery.gaea.api.src.BitCoinData;
import com.tincery.gaea.api.src.QQData;
import com.tincery.gaea.api.src.extension.BitCoinExtension;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * 0.syn 1.fin
 * Time: 2.startTime 3.endTime
 * Flow: 4.uppkt 5.upbyte 6.downpkt 7.downbyte
 * 8.datatype(1 / -1)
 * 七元组：9.protocol 10.serverMac 11.clientMac 12.serverIp_n 13.clientIp_n 14.serverPort 15.clientPort
 * 16.source 17.runleName
 * 手机：18.imsi 19.imei 20.msisdn
 * 五元组：21.outclientip 22.outserverip 23.outclientport 24.outserverport 25.outproto
 * 26.userid 27.serverid
 * 28.ismac2outer
 *-------------dataType == 1--------------
 * 29.serverVersion 30.numberOfPayload
 * data1 ... data n
 * ------------dataType == -1 --------------
 * 29.upPayload 30.downPayload
 */
@Component
@Slf4j
public class BitCoinAnalysis implements SrcLineAnalysis<BitCoinData> {


    @Autowired
    public SrcLineSupport srcLineSupport;



    /**
     * 封装到来的qq数据
     */
    @Override
    public BitCoinData pack(String line) {
        BitCoinData bitCoinData = new BitCoinData();
        String[] elements = StringUtils.FileLineSplit(line);
        fixCommon(bitCoinData,elements);

        Integer dataType = bitCoinData.getDataType();
        if (dataType == -1){
            fixMalformed(bitCoinData,elements);
        }else if (dataType == 1){
            fixNormal(bitCoinData,elements);
        }

        return bitCoinData;
    }

    /**
     * 装填正常情况下的data1 ... data n
     * @param bitCoinData 需要装填的实体
     * @param elements 数据
     */
    private void fixNormal(BitCoinData bitCoinData, String[] elements) {
        if (!haveExtension(elements)){
            return;
        }
        ArrayList<BitCoinExtension> bitCoinExtensions = new ArrayList<>();
        bitCoinData.setVersion(Integer.parseInt(elements[29]))
                .setSize(Integer.parseInt(elements[30]));
        for (int i = 29; i < elements.length; i++){
            String element = elements[i];
            if (element.charAt(0) == '('){
                BitCoinExtension extensionKey = getExtensionKey(element);
                bitCoinExtensions.add(extensionKey);
            }
        }
        bitCoinData.setBitCoinExtension(bitCoinExtensions);
    }

    /**
     * 这里的传进来的String 结构为(sigr:)数据1(sigs:)数据2(key:)数据3
     * 获得数据1,2,3分别填装
     * @param extensionString 传递进来的数据字符串
     * @return extension
     */
    private BitCoinExtension getExtensionKey(String extensionString){
        String temp = extensionString.substring(7);
        String[] split = temp.split("\\(sigs:\\)");
        String sigr = split[0];
        String[] sigsAndKey = split[1].split("\\(key:\\)");
        String sigs = sigsAndKey[0];
        String key = sigsAndKey[1];
        return new BitCoinExtension(sigr, sigs, key);
    }



    /**
     * 判断元素是否有extension
     * @param elements 数据源
     * @return boolean
     */
    private boolean haveExtension(String[] elements){
        if (elements.length <= 29){
            return false;
        }else if (elements.length == 31 && Objects.equals(elements[30],"0")){
            return false;
        }else {
            return elements.length > 31 && !Objects.equals(elements[30], "0");
        }
    }

    /**
     * 装填malformed需要的属性
     * @param bitCoinData 需要装填的实体
     * @param elements 数据
     */
    private void fixMalformed(BitCoinData bitCoinData, String[] elements) {
        this.srcLineSupport.setMalformedPayload(elements[29],elements[30],bitCoinData);
    }

    /**
     * 装填公共common
     * @param bitCoinData 要装填的实体
     * @param elements 数据
     */
    private void fixCommon(BitCoinData bitCoinData, String[] elements) {
        this.srcLineSupport.setSynAndFin(elements[0],elements[1],bitCoinData);
        this.srcLineSupport.setTime(elements[2],elements[3],bitCoinData);
        this.srcLineSupport.setFlow(elements[4],elements[5],elements[6],elements[7],bitCoinData);
        bitCoinData.setDataType(Integer.parseInt(elements[8]));
        this.srcLineSupport.set7Tuple(elements[10],elements[11],elements[12],elements[13],elements[14],elements[15],elements[9],"BITCOIN",bitCoinData);
        bitCoinData.setSource(elements[16]);
        this.srcLineSupport.setTargetName(elements[17],bitCoinData);
        this.srcLineSupport.setGroupName(bitCoinData);
        this.srcLineSupport.setMobileElements(elements[18],elements[19],elements[20],bitCoinData);
        this.srcLineSupport.set5TupleOuter(elements[21],elements[22],elements[23],elements[24],elements[25],bitCoinData);
        this.srcLineSupport.setPartiesId(elements[26],elements[27],bitCoinData);
        this.srcLineSupport.setIsMac2Outer(elements[28],bitCoinData);
        try {
            this.srcLineSupport.isForeign(bitCoinData.getServerIp());
        }catch (RuntimeException e){
            bitCoinData.setForeign(false);
            log.warn("无法解析内外网ipv6，数据为{}", Arrays.asList(elements));
        }
    }

}
