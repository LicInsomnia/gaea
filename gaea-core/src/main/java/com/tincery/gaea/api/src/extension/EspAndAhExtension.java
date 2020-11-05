package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspAndAhExtension {

    private String c2sSpi;
    private String s2cSpi;
    private String seqNum;
    private String upPayload;
    private String downPayload;

    public void merge(EspAndAhExtension extension) {
        if (null == this.upPayload) {
            this.upPayload = extension.getUpPayload();
        }
        if (null == this.downPayload) {
            this.downPayload = extension.getDownPayload();
        }
        if ("".equals(this.c2sSpi)) {
            this.c2sSpi = extension.c2sSpi;
        }
        if ("".equals(this.s2cSpi)) {
            this.s2cSpi = extension.s2cSpi;
        }
    }

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                this.c2sSpi, this.s2cSpi, this.upPayload, this.downPayload
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
