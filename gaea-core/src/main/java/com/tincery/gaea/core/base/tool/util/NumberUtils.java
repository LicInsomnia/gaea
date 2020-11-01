package com.tincery.gaea.core.base.tool.util;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class NumberUtils {
    public static long sum(Long a, Long b) {
        return (a == null ? 0 : a) + (b == null ? 0 : b);
    }

    public static int sum(Integer a, Integer b) {
        return (a == null ? 0 : a) + (b == null ? 0 : b);
    }
}
