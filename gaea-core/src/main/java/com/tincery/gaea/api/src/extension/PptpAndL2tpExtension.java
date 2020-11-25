package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PptpAndL2tpExtension implements Serializable {

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
     * 挑战名
     */
    private String challengeName;
    /**
     * 认证协议
     */
    private String authenticationProtocol;
    /**
     * 认证算法
     */
    private String authenticationAlgorithm;
    /**
     * 成功消息
     */
    private String successMessage;
    /**
     * 加密算法
     */
    private String encryptionAlgorithm;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                this.challenge, this.challengeName, this.response, this.responseName, this.authenticationProtocol, this.authenticationAlgorithm, this.successMessage, this.encryptionAlgorithm
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
