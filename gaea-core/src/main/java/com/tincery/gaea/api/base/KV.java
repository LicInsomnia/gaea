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
public class KV<K,V> implements Serializable {
    private K key;
    private V value;

    public KV(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public KV() {
    }

    public static <K,V> KV<K,V> of(K key, V value){
            return new KV<>(key,value);
    }
}
