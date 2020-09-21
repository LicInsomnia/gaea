package com.tincery.gaea.core.base.component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * Src层通用配置
 **/
@Setter
@Getter
public abstract class AbstractSrcCommonProperties {

    private String category;

    /***是否备份原始txt文件至TINCERY_DATA/bak下*/
    private boolean bak;

    /***是否为离线任务*/
    private boolean offLine;

    /***是否是测试程序**/
    private boolean test;

    /***src根目录*/
    private String srcPath;

    /***缓存最大xxx行记录输出一次csv*/
    private int maxLine = 30000;

    /***一次最多处理xxx个文件，按文件时间最老的开始处理*/
    private int maxFile = 10;

    /***多线程数*/
    private int executor = 0;




}
