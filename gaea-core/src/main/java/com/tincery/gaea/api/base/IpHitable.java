package com.tincery.gaea.api.base;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface IpHitable {

    /****
     * IP匹配是否命中
     * @author gxz
     * @param ip ip
     * @param protocol protocol
     * @param port  port
     * @return boolean 是否符合条件
     **/
    boolean hit(long ip, int protocol, int port);
}
