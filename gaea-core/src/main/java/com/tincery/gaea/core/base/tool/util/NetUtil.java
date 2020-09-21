package com.tincery.gaea.core.base.tool.util;


import com.tincery.gaea.core.base.tool.ToolUtils;
import javafx.util.Pair;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class NetUtil extends cn.hutool.core.net.NetUtil {


    /**
     * ipv4最小最大值
     */
    public final static long MIN_IPV4 = 0L;

    public final static long MAX_IPV4 = 4294967295L;

    /****
     * 通过IP段字符串 返回long值ip范围
     * @author gxz
     * @param ip "127.0.0.1/15"
     * @return javafx.util.Pair<java.lang.Long,java.lang.Long>
     **/
    public static Pair<Long,Long> getRange(String ip){
        long ipN = ToolUtils.IP2long(ip.split("/")[0]);
        int tag = (int) Math.pow(2.0, (32 - Double.parseDouble(ip.split("/")[1])));
        return new Pair<>(ipN, ipN + tag - 1);
    }


}
