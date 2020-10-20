package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

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
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.domain, this.responseIp, this.malformedUpPayload, this.malformedDownPayload, this.extension};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
