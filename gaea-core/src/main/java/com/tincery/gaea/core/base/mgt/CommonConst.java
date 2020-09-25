package com.tincery.gaea.core.base.mgt;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 通用常量维护
 **/
@Slf4j
public class CommonConst {

    /**
     * 默认字符编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    private CommonConst(){
        throw new RuntimeException();
    }


    public final static int KB = 1024;
    public final static int MB = 1024 * KB;
    public final static long GB = 1024 * MB;
    public final static long TB = 1024 * GB;



}
