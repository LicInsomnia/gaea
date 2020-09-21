package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.starter.base.InitializationRequired;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 规则接口
 **/
public interface Rule extends InitializationRequired {
    /****
     * 规则匹配
     * @author gxz
     * @param data 被匹配的一条数据
     * @return boolean 是否匹配
     **/
    boolean matchOrStop(AbstractSrcData data);

    boolean isActivity();


}
