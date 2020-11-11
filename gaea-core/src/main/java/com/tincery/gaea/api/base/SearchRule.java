package com.tincery.gaea.api.base;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SearchRule {

    private int count;
    private int countType;
    private KV<String, List<String>> match;
    private Map<Integer,Integer> forward;
    private List<String> out;

}
