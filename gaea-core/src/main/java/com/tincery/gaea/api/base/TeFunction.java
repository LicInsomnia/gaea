package com.tincery.gaea.api.base;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface TeFunction<A, B, C, D> {
    D apply(A a, B b, C c);
}
