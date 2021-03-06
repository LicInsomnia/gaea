package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.SslExtension;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Insomnia
 */
@Setter
@Getter
public class SslData extends AbstractSrcData {

    private SslExtension sslExtension;

    @Override
    public void adjust() {
        super.adjust();
        adjustDaulAuth();
        adjustSha1();
    }

    @Override
    public void adjustCompleteSession() {
        if (this.syn && this.sslExtension.isHasApplicationData()) {
            this.completeSession = true;
        }
    }

    protected void adjustDaulAuth() {
        if (null == this.sslExtension.getClientCerChain() && null == this.sslExtension.getServerCerChain()) {
            return;
        }
        if (null != this.sslExtension.getClientCerChain() && null != this.sslExtension.getServerCerChain()) {
            this.completeSession = true;
            return;
        }
        this.completeSession = false;
    }

    protected void adjustSha1() {
        if (null == this.sslExtension.getServerCerChain()) {
            return;
        }
        this.sslExtension.setSha1(this.sslExtension.getServerCerChain().get(0).getSha1());
    }

    @Override
    public String toCsv(char splitChar) {
        String extensionElements = null;
        String extension = null;
        if (null != this.sslExtension) {
            extensionElements = this.sslExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.sslExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.completeSession,
                this.malformedUpPayload, this.malformedDownPayload,
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
