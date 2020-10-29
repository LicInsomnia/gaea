package com.tincery.gaea.core.base.tool.util;

import com.google.common.base.Joiner;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com / insomnia 针对gaea.source层数据处理解析txt中部分限定规范提供方法
 **/
public class SourceFieldUtils {

    /**
     * 实体中部分String字段赋值时，若txt中该值为0则代表null
     *
     * @param str txt中字符串
     * @return 返回值
     */
    public static String parseStringStr(String str) {
        if (StringUtils.isNotEmpty(str)) {
            return str.equals("0") ? null : str;
        }
        return null;
    }

    /**
     * 实体中部分string字段赋值时，若txt中该值为empty"" 则强制赋值为null
     *
     * @param str txt中字符串
     * @return 返回值
     */
    public static String parseStringStrEmptyToNull(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str;
    }

    /**
     * 实体中部分Boolean字段赋值时，对应txt中int值：1 -> TRUE; 0 -> FALSE; other -> null
     *
     * @param intStr txt中字符串
     * @return 返回值
     */
    public static Boolean parseBooleanStr(String intStr) {
        if (StringUtils.isNotEmpty(intStr)) {
            switch (intStr) {
                case "0":
                    return false;
                case "1":
                    return true;
                default:
                    return null;
            }
        }
        return null;
    }

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

    /**
     * 集合转字符串
     *
     * @param collection 带转换集合
     * @param splitChar  分隔符
     * @return 转换后的字符串
     */
    public static String formatCollection(Collection<?> collection, String splitChar) {
        String result = null;
        if (!CollectionUtils.isEmpty(collection)) {
            result = Joiner.on(splitChar).useForNull("").join(collection);
        }
        return result;
    }

    public static String formatCollection(Collection<?> collection) {
        return formatCollection(collection, ";");
    }

    public static String formatStringBuilder(StringBuilder stringBuilder, String splitChar) {
        if (stringBuilder != null && stringBuilder.length() != 0) {
            List<String> collection = Arrays.asList(stringBuilder.toString().split(","));
            return formatCollection(collection, splitChar);
        }
        return null;
    }

    public static Object mergeField(Object obj1, Object obj2) {
        return null == obj1 ? obj2 : obj1;
    }


}
