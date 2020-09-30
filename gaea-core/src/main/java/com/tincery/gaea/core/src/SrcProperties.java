package com.tincery.gaea.core.src;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gxz gongxuanzhang@foxmail.com Src层通用配置
 **/
@Setter
@Getter
@ConfigurationProperties (prefix = "src")
public class SrcProperties {


    /***是否备份原始文件至/back下*/
    private boolean back;

    /***是否是测试程序**/
    private boolean test;

    /***缓存最大xxx行记录输出一次csv*/
    private int maxLine = 30000;

    /***多线程数*/
    private int executor = 0;


}
