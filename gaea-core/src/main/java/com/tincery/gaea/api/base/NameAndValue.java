package com.tincery.gaea.api.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class NameAndValue implements Serializable {
    private String name;
    private Object value;

    public NameAndValue(){

    }

    public NameAndValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
