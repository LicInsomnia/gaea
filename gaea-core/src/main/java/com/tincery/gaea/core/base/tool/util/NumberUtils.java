package com.tincery.gaea.core.base.tool.util;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class NumberUtils {

    /***
     * txt中代表此字段的字符串  转化为数字  如果没有值则返回null
     * 如果intStr 为 "0" 也代表此内容没有值
     * @param intStr 字符串
     * @return 返回的内容
     **/
    public static Integer parseIntegerStr(String intStr) {
        if (StringUtils.isNotEmpty(intStr)) {
            int i = Integer.parseInt(intStr);
            return i == 0 ? null : i;
        }
        return null;

    }
}
