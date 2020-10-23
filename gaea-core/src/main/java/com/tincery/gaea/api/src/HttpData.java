package com.tincery.gaea.api.src;

import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.base.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HttpData extends AbstractSrcData{

    private String host;
    private String method;
    private String urlRoot;
    private String userAgent;
    private String contentLength;
    private Boolean isResponse;

    private Location serverIpInfo;
    private Location clientIpInfo;
    /**
     * 用来保存上下行数据
     */
    private String payload;

    /**
     * 用来保存sub 截取的key
     */
    private String key;
    /**
     * 用来保存http行解析器入参截取的key
     */
    private String subName;

    public List<HttpMeta> metas;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getSyn(), this.getFin(),
                this.host,this.method,this.urlRoot,this.userAgent,this.contentLength,
                this.malformedUpPayload,this.malformedDownPayload,this.extension};
        return Joiner.on(splitChar).useForNull("").join(join);
    }
}
