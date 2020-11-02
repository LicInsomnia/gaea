package com.tincery.gaea.api.base;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum ProtocolType {

    TCP(6),
    UDP(17),
    ESP(50),
    AH(51),
    GRE(47);

    private final int value;

    public int getValue() {
        return value;
    }

    ProtocolType(int value) {
        this.value = value;
    }


}
