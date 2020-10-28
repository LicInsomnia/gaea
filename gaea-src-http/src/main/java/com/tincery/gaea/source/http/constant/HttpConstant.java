package com.tincery.gaea.source.http.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 独属于Http的常量
 * 因为要区分http的入参 Entry(String,byte[])
 * String + '常量' + value：new String(byte[])分隔区分
 * 又因为http的入参（.dat文件）中既有中文又有；所以定义独特的常量用来分隔
 */
public class HttpConstant {
    public static final String HTTP_CONSTANT = "苍叔牛逼";

    /**
     * 合法的http头
     */
    public static Set<String> legelHeader = new HashSet<String>() {{
        add("GET ");
        add("POST");
        add("PUT ");
        add("HEAD");
        add("DELE");
        add("OPTI");
        add("HTTP");
    }};
}
