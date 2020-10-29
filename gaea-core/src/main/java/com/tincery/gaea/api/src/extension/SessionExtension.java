package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionExtension {

    /**
     * 上下行载荷内容
     */
    private String upPayLoad;
    private String downPayLoad;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                this.upPayLoad, this.downPayLoad
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
