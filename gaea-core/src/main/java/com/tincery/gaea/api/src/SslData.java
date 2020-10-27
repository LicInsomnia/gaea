package com.tincery.gaea.api.src;


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

    SslExtension sslExtension;

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
        this.sslExtension.setSha1(this.sslExtension.getServerCerChain().get(0));
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.duration, this.syn, this.fin,
                this.malformedUpPayload, this.malformedDownPayload,
                this.sslExtension.toCsv(splitChar)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
