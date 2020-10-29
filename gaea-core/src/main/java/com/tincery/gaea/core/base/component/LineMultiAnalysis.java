package com.tincery.gaea.core.base.component;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface LineMultiAnalysis<INPUT, OUTPUT> {

    /****
     * 将输入包装成多个输出
     * @author gxz
     * @param input 输入类型
     **/
    OUTPUT[] pack(INPUT input);

}
