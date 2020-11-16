package com.tincery.gaea.api.base;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SearchRule {
    /**
     * 需要命中次数
     */
    private int count;
    /**
     * 命中标记，
     * -1: <=命中次数
     * 0 : == 命中次数
     * 1: >= 命中次数
     */
    private int countType;

    private KV<String, List<String>> match;
    /**
     * 转发集合 null意为不转发
     */
    private Map<Integer, Integer> forward;
    private List<String> out;

}
