package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.base.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class HttpData extends AbstractSrcData {


    private String host;
    private String method;
    private String urlRoot;
    private String userAgent;
    private String contentLength;
    private Boolean isResponse;
    private Location serverIpLocation;
    private Location clientIpLocation;
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
    }
    /**
     * 整理Meta链表数据，调整顺序
     * 合并字段
     *
     */
    private void adjustMetas() {
        if (Objects.isNull(this.metas)){
            return;
        }
        this.metas.sort(Comparator.comparingInt(HttpMeta::getIndex));

        ArrayList<HttpMeta> requestList = new ArrayList<>();
        ArrayList<HttpMeta> responseList = new ArrayList<>();
        for (HttpMeta meta : this.metas) {
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
            HttpMeta requestHttpMeta = requestList.get(i);
            result.add(requestHttpMeta.fixContentAndOther(responseList.get(i)));
        }

        /*
        处理剩余的请求和响应
         */
//        result.add();

        this.setMetas(result);

    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getSyn(), this.getFin(),
                this.host, this.method, this.urlRoot, this.userAgent, this.contentLength,
                this.malformedUpPayload, this.malformedDownPayload, this.extension};
        return Joiner.on(splitChar).useForNull("").join(join);
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
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(element);
            result.add(jsonObject);
        }
        return result;
    }

}
