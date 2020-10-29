package com.tincery.gaea.core.base.tool.util;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String DEFAULT_SEP = new String(new char[]{0x07});

    private StringUtils() {
        throw new RuntimeException("禁止创建实体");
    }

    /*****
     * 参数只要有一个为空 返回false
     * @author gxz
     * @date 2020/8/14
     * @return boolean
     **/
    public static boolean notAllowNull(String... strings) {
        for (String string : strings) {
            if (isEmpty(string)) {
                return false;
            }
        }
        return true;
    }

    /****
     * 检测一个字符串是否符合标准
     * @author gxz
     * @param str 被判断的字符串
     * @param prefix 前缀
     * @param contains 包含的内容
     * @param extension 结尾
     * @return boolean
     **/
    public static boolean checkStr(String str, String prefix, String contains, String extension) {
        prefix = prefix == null ? "" : prefix;
        contains = contains == null ? "" : contains;
        extension = extension == null ? "" : extension;
        if (isEmpty(str)) {
            return false;
        }
        return str.startsWith(prefix) && str.endsWith(extension) && str.contains(contains);
    }

    /****
     * @see StringUtils#checkStr(String, String, String, String)
     * @author gxz
     **/
    public static boolean checkStr(String str, String prefix, String extension) {
        return checkStr(str, prefix, "", extension);
    }

    public static String[] FileLineSplit(String line) {
        return line.split(DEFAULT_SEP, -1);
    }


}
