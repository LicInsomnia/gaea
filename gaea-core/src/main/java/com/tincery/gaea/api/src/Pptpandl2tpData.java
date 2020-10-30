package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

/**
 * 这个类独有的字段还不知道什么数据格式  和意思  暂定是以下的这些
 */
@Getter
@Setter
public class Pptpandl2tpData extends AbstractSrcData {

    /**
     * 响应
     */
    private String response;

    /**
     * 挑战
     */
    private String challenge;

    /**
     * 响应名
     */
    private String responseName;

    /**
     * 挑战名？
     */
    private String challengeName;

    /**
     * 认证协议
     */
    private String authProtocol;

    /**
     * 认证算法
     */
    private String authAlgo;

    /**
     * 成功消息
     */
    private String successMesg;
    /**
     * alog是对话  enc不知道
     */
    private String encAlog;


    @Override
    public void adjust() {
        super.adjust();
    }


    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.duration, this.getSyn(), this.getFin(),
                this.malformedUpPayload, this.malformedDownPayload,this.response,
                this.challenge,this.responseName,this.challengeName,this.authProtocol,this.authAlgo,this.successMesg,this.encAlog
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
