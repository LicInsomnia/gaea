package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Administrator
 */
@Getter
@Setter
public class HttpData extends AbstractSrcData {


    private List<String> host;
    private List<String> method;
    private List<String> urlRoot;
    private List<String> userAgent;
    private Integer contentLength;
    private Boolean isResponse;
    private Location serverLocation;
    private Location clientLocation;
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
        adjustMetas();
        adjustHttpData();
    }

    private void adjustHttpData(){
        if (Objects.isNull(this.metas)){
            return;
        }
        this.host = this.metas.stream().map(HttpMeta::getHost).collect(Collectors.toList());
        this.host = fixCollection(this.host);
        this.method = this.metas.stream().filter(item-> item.getMethod().toString().contains(">>"))
                .map(HttpMeta::getMethod).map(Object::toString).collect(Collectors.toList());
        this.method = fixCollection(this.method);
        this.urlRoot = this.metas.stream().map(HttpMeta::getUrlRoot).collect(Collectors.toList());
        this.urlRoot = fixCollection(this.urlRoot);
        this.userAgent = this.metas.stream().map(HttpMeta::getUserAgent).collect(Collectors.toList());
        this.userAgent = fixCollection(this.userAgent);

        this.contentLength = this.metas.stream().mapToInt(httpMeta->{
            if (httpMeta.getResponseContentLength()!=null && httpMeta.getResponseContentLength()!=0){
                return httpMeta.getResponseContentLength();
            }else if (httpMeta.getRequestContentLength()!=null && httpMeta.getRequestContentLength()!=0){
                return httpMeta.getRequestContentLength();
            }else{
                return 0;
            }
        }).sum();

    }
    /**
     * 整理Meta链表数据，调整顺序
     * 合并字段
     *
     */
    private void adjustMetas() {
        if (Objects.isNull(this.getMetas())){
            return;
        }
        this.getMetas().sort(Comparator.comparingInt(HttpMeta::getIndex));

        ArrayList<HttpMeta> requestList = new ArrayList<>();
        ArrayList<HttpMeta> responseList = new ArrayList<>();
        for (HttpMeta meta : this.getMetas()) {
            if (StringUtils.isEmpty(meta.getRequest())){
                responseList.add(meta);
                continue;
            }
            requestList.add(meta);
        }
        if (CollectionUtils.isEmpty(requestList) || CollectionUtils.isEmpty(responseList)){
            //如果两个集合有一个为空 那么无法合并
            for (HttpMeta meta : metas) {
                meta.fixContentByHalfEmpty();
            }
            return;
        }
        /*
        开始合并字段
         */
        ArrayList<HttpMeta> result = new ArrayList<>();

        int index = Math.min(requestList.size(),responseList.size());

        for (int i = 0; i < index; i++) {
            HttpMeta request = requestList.get(i);
            HttpMeta response = responseList.get(i);
            HttpMeta httpMeta = fixContentAndOther(request, response);
            result.add(httpMeta);
        }
        /*
        处理剩余的请求和响应
         */
        if (index!=requestList.size()){
            result.addAll(requestList);
        }else if (index!= responseList.size()){
            result.addAll(responseList);
        }
        this.setMetas(result);
    }
    private HttpMeta fixContentAndOther(HttpMeta request,HttpMeta response){
        request.setContent(request.getRequest() + HeadConst.GORGEOUS_DIVIDING_LINE +"\r\n"+ response.getResponse());
        request.setRepHeaders(response.repHeaders);
        request.setMethod(request.getMethod().append((">>")).append(response.getMethod()));
        return request;
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getSyn(), this.getFin(),
                SourceFieldUtils.formatCollection(this.host), SourceFieldUtils.formatCollection(this.method),
                SourceFieldUtils.formatCollection(this.urlRoot), SourceFieldUtils.formatCollection(this.userAgent),
                this.contentLength,
                this.malformedUpPayload, this.malformedDownPayload, SourceFieldUtils.formatCollection(httpMetaToJsonString(this.metas)),
                this.extension};
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    private List<String> httpMetaToJsonString(List<HttpMeta> metaList){
        if (CollectionUtils.isEmpty(metaList)){
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        metaList.forEach(meta->{
           result.add(JSONObject.toJSON(meta).toString());
        });

        return result;
    }

    /**
     * 合并两个httpData的数据
     * @param httpData
     */
    public void merge(HttpData httpData) {
        List<HttpMeta> newMetas = this.getMetas();
        if (CollectionUtils.isEmpty(newMetas)){
            newMetas = new ArrayList<>();
        }
        List<HttpMeta> oldMetas = httpData.getMetas();
        if (CollectionUtils.isEmpty(oldMetas)){
            oldMetas = new ArrayList<>();
        }
        newMetas.addAll(oldMetas);
        this.setMetas(newMetas);
        this.setCapTime(Math.min(this.getCapTime(),httpData.getCapTime()));

        long endTime = Math.max(this.getCapTime() + this.getDuration(), httpData.getCapTime() + httpData.getDuration());
        this.setDuration(endTime - this.getCapTime());


    }

    public List<JSONObject> toJsonObjects() {
        List<JSONObject> result = new ArrayList<>();
        List<HttpMeta> metas = this.getMetas();
        if (CollectionUtils.isEmpty(metas)){
            metas = new ArrayList<>();
        }
        for (HttpMeta meta : metas) {
            HttpElement element = new HttpElement();
            element.init(this,meta);
            if (element.getIsMalformed()){
                continue;
            }
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(element);
            result.add(jsonObject);
        }
        return result;
    }

    private List<String> fixCollection(List<String> collection){
        if (CollectionUtils.isEmpty(collection)){
            collection = new ArrayList<>();
        }
        return collection;
    }

}
