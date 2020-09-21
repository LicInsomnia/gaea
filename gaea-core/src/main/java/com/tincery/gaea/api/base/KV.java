package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author gxz
 * @date 2020/4/13 14:42
 **/
@Setter
@Getter
public class KV implements Serializable {
    private String key;
    private String value;
}
