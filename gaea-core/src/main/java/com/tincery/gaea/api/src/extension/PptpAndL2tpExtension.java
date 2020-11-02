package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PptpAndL2tpExtension {

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

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                this.response, this.challenge, this.responseName, this.challengeName, this.authProtocol, this.authAlgo, this.successMesg, this.encAlog
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
