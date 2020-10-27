package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DnsExtension {

    /**
     * DNS请求域名
     */
    String domain;
    /**
     * DNS请求cname
     */
    Set<String> cname;
    /**
     * DNS响应IP
     */
    Set<String> responseIp;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                this.domain,
                SourceFieldUtils.formatCollection(this.cname),
                SourceFieldUtils.formatCollection(this.responseIp)};
        return Joiner.on(splitChar).useForNull("").join(join);

    }

}
