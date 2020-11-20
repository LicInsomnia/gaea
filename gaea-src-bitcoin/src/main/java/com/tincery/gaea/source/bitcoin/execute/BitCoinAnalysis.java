package com.tincery.gaea.source.bitcoin.execute;


import com.tincery.gaea.api.src.QQData;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gxz
 */

//TODO 换读取模型
@Component
public class BitCoinAnalysis implements SrcLineAnalysis<QQData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /**
     * 封装到来的qq数据
     */
    @Override
    public QQData pack(String line) {

        return new QQData();
    }

}
