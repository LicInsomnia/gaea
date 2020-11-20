package com.tincery.gaea.core.dw;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface MergeAble<T> {

    T merge(T t);

    String getId();
}
