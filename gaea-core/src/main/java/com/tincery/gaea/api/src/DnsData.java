package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class DnsData extends AbstractSrcData {


    /**
     * DNS请求域名
     */
    String domain;
    /**
     * DNS响应IP
     */
    Set<String> responseIp;

    @Override
    public void adjust() {
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getDurationTime() / 1000, this.getSyn(), this.getFin()};
        return Joiner.on(splitChar).join(join);
    }


}
