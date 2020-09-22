package com.tincery.gaea.core.base.component;


import com.tincery.gaea.api.base.AbstractMetaData;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 行解析器
 **/
public interface LineAnalysis<INPUT, OUTPUT extends AbstractMetaData> {


    /****
     * 解析一行 封装成实体
     * @param input 一行数据
     * @return T 解析完成的实体
     **/
    public OUTPUT pack(INPUT input);

}
