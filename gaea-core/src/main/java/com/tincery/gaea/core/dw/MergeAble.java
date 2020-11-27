package com.tincery.gaea.core.dw;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface MergeAble<T> {

    T merge(T t);

    String getId();


    static <M extends MergeAble<M>> M merge(List<M> list) {
        M mergeAble = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            M mergeAble1 = list.get(i);
            mergeAble.merge(mergeAble1);
        }
        return mergeAble;
    }
}
