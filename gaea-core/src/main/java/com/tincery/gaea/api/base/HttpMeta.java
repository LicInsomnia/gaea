package com.tincery.gaea.api.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.LevelDomainUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class HttpMeta {
    public String url;
    public String host;
    public StringBuilder method;
    public String sld;
    public String tld;
    public Integer requestContentLength;
    public Integer responseContentLength;
    @JSONField(serialize = false)
    public String contentType;
    public String userAgent;
    @JSONField(serialize = false)
    public String acceptLanguage;
    @JSONField(serialize = false)
    public String from;
    @JSONField(serialize = false)
    public Boolean authorization;
    @JSONField(serialize = false)
    public Boolean proxyauth;
    //public List<Map<String, String>> headers;
    public List<Map<String, String>> reqHeaders;
    public List<Map<String, String>> repHeaders;
    @JSONField(serialize = false)
    public String acceptEncoding;
    public String urlRoot;
    public String params;
    @JSONField(serialize = false)
    public boolean hasResponse;
    @JSONField(serialize = false)
    public boolean isMalformed = true;
    @JSONField(serialize = false)
    public String request;
    @JSONField(serialize = false)
    public String response;
    /**
     * http内容 用request和response字段合并
     */
    public String content;
    /**
     * 该index为meta的顺序
     */
    @JSONField(serialize = false)
    public Integer index;


    public void setContent(String content, boolean isResponse) {
        StringBuilder subContent;
        if (content.length() > 8192) {
            subContent = new StringBuilder(content.substring(0, 8192));
            subContent.append("...");
        } else {
            subContent = new StringBuilder(content);
        }
        if (isResponse) {
            this.response = subContent.toString();
        } else {
            this.request = subContent.toString();
        }
    }

    public void addMethod(String method, boolean before) {
        StringBuilder methodBuilder = new StringBuilder(method);
        if (Objects.isNull(this.method)) {
            this.method = new StringBuilder();
        }
        if (before) {
            this.method = connectBuilder(methodBuilder, this.method);
        } else {
            appendBuilder(this.method, method);
        }
    }

    private StringBuilder connectBuilder(StringBuilder request, StringBuilder response) {
        StringBuilder result = new StringBuilder();
        if (!request.toString().equals("null")) {
            appendBuilder(result, request.toString());
        }
        appendBuilder(result, response.toString());
        return result;
    }

    private void appendBuilder(StringBuilder builder, String content) {
        if (null == content || content.isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(">>");
        }
        builder.append(content);
    }

    public void setResponseHeaders(String key, String value) {
        if (null == this.repHeaders) {
            this.repHeaders = new ArrayList<>();
        }
        Map<String, String> head = new HashMap<>();
        head.put("key", key);
        head.put("value", value);
        this.repHeaders.add(head);
    }

    public void setRequestHeaders(String key, String value) {
        if (null == this.reqHeaders) {
            this.reqHeaders = new ArrayList<>();
        }
        Map<String, String> head = new HashMap<>();
        head.put("key", key);
        head.put("value", value);
        this.reqHeaders.add(head);
    }

    /**
     * 请求响应一方为空时执行的方法
     */
    public void fixContentByHalfEmpty() {
        this.content = StringUtils.isEmpty(this.request) ? this.response : this.request;
    }

    /**
     * 请求和响应合并字段的方法
     *
     * @param response 响应参数
     */
    public HttpMeta fixContentAndOther(HttpMeta response) {

        this.content = this.getRequest() + "\r\n" + HeadConst.GORGEOUS_DIVIDING_LINE + "\r\n" + response.getResponse();
        this.repHeaders = response.repHeaders;
        this.method.append(">>").append(response.method);
        return this;
    }


    public void setUrl(String url) {
        this.url = url;
        if (url.contains("?")) {
            String[] spc = url.split("\\?");
            if (spc.length >= 2) {
                this.urlRoot = spc[0];
                String paraPairs = url.substring(spc[0].length() + 1);
                StringBuilder para = new StringBuilder();
                for (String paraPair : paraPairs.split("&")) {
                    String paraKey = paraPair.split("=")[0];
                    if (!paraKey.isEmpty()) {
                        para.append(paraKey).append("&");
                    }
                }
                if (para.length() > 0) {
                    this.params = para.substring(0, para.length() - 1);
                }
            }
        } else {
            this.urlRoot = url;
        }
    }

    public void setHost(String host) {
        this.host = host.toLowerCase();
        this.sld = LevelDomainUtils.SLD(this.host);
        this.tld = LevelDomainUtils.TLD(this.host);
    }
}
