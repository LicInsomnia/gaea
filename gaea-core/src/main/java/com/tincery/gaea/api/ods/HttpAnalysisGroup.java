package com.tincery.gaea.api.ods;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * HTTP解析之后可以操作的内容
 **/
@Setter
@Getter
public class HttpAnalysisGroup implements Serializable {

    private List<JSONObject> noHostList;
    private List<JSONObject> noMatchStrList;
    private List<JSONObject> noHitList;
    private List<JSONObject> trashList;
    private List<JSONObject> successList;


    public HttpAnalysisGroup merge(HttpAnalysisGroup that) {
        this.noHostList.addAll(that.getNoHostList());
        this.noMatchStrList.addAll(that.getNoMatchStrList());
        this.noHitList.addAll(that.getNoHitList());
        this.trashList.addAll(that.getTrashList());
        this.successList.addAll(that.getSuccessList());
        return this;
    }

    public static HttpAnalysisGroup concurrentInit() {
        HttpAnalysisGroup httpAnalysisGroup = new HttpAnalysisGroup();
        httpAnalysisGroup.noHostList = new CopyOnWriteArrayList<>();
        httpAnalysisGroup.noMatchStrList = new CopyOnWriteArrayList<>();
        httpAnalysisGroup.noHitList = new CopyOnWriteArrayList<>();
        httpAnalysisGroup.trashList = new CopyOnWriteArrayList<>();
        httpAnalysisGroup.successList = new CopyOnWriteArrayList<>();
        return httpAnalysisGroup;
    }

    public static HttpAnalysisGroup init(){
        HttpAnalysisGroup httpAnalysisGroup = new HttpAnalysisGroup();
        httpAnalysisGroup.noHostList = new ArrayList<>();
        httpAnalysisGroup.noMatchStrList = new ArrayList<>();
        httpAnalysisGroup.noHitList = new ArrayList<>();
        httpAnalysisGroup.trashList = new ArrayList<>();
        httpAnalysisGroup.successList = new ArrayList<>();
        return httpAnalysisGroup;
    }
}
