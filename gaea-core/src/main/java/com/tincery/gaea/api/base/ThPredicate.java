package com.tincery.gaea.api.base;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface ThPredicate<A, B, C> {
    boolean test(A a, B b, C c);
}
