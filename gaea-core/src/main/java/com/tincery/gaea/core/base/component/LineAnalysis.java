package com.tincery.gaea.core.base.component;


import com.tincery.gaea.api.base.AbstractMetaData;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 行解析器
 **/
public interface LineAnalysis<T extends AbstractMetaData> {

    /**
     * SRC层默认分隔符
     */
    public static String SRC_SEPARATOR = new String(new char[]{0x07});

    /****
     * 解析一行 封装成实体
     * @param line 一行数据
     * @return T 解析完成的实体
     **/
    public T pack(String line);

}
