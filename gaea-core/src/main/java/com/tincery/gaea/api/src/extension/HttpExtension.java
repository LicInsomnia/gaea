package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class HttpExtension implements Serializable {

    private List<String> host;
    private List<String> method;
    private List<String> urlRoot;
    private List<String> userAgent;
    private Integer contentLength;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                SourceFieldUtils.formatCollection(this.host), SourceFieldUtils.formatCollection(this.method),
                SourceFieldUtils.formatCollection(this.urlRoot), SourceFieldUtils.formatCollection(this.userAgent),
                this.contentLength};
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
