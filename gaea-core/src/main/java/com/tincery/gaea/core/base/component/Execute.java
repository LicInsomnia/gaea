package com.tincery.gaea.core.base.component;

import com.tincery.starter.base.InitializationRequired;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

public interface Execute extends InitializationRequired {

    /*****
     * 执行器核心方法 具体执行内容
     * @author gxz
     **/
    void execute() throws IllegalAccessException;

}
