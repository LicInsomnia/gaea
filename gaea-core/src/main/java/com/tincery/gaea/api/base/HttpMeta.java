package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HttpMeta {
    public String url;
    public String host;
    public StringBuilder method = new StringBuilder();
    public String secondHost;
    public String topHost;
    public Integer requestContentLength;
    public Integer responseContentLength;
    public String contentType;
    public String userAgent;
    public String acceptLanguage;
    public String from;
    public Boolean authorization;
    public Boolean proxyauth;
    //public List<Map<String, String>> headers;
    public List<Map<String, String>> req_headers;
    public List<Map<String, String>> rep_headers;
    public String acceptEncoding;
    public String urlRoot;
    public String parameter;
    public boolean hasResponse;
    public boolean isMalformed = true;
    public String request;
    public String response;


    public void setContent(String content, boolean isResponse) {
        StringBuilder subContent;
        if(content.length() > 4096) {
            subContent = new StringBuilder(content.substring(0, 4096));
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
        if (before) {
            this.method = connectBuilder(methodBuilder, this.method);
        } else {
            appendBuilder(this.method, method);
        }
    }

    private StringBuilder connectBuilder(StringBuilder request, StringBuilder response) {
        StringBuilder result = new StringBuilder();
        if(!request.toString().equals("null")) {
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
        if (null == this.rep_headers) {
            this.rep_headers = new ArrayList<>();
        }
        Map<String, String> head = new HashMap<>();
        head.put("key", key);
        head.put("value", value);
        this.rep_headers.add(head);
    }

    public void setRequestHeaders(String key, String value) {
        if (null == this.req_headers) {
            this.req_headers = new ArrayList<>();
        }
        Map<String, String> head = new HashMap<>();
        head.put("key", key);
        head.put("value", value);
        this.req_headers.add(head);
    }
}
