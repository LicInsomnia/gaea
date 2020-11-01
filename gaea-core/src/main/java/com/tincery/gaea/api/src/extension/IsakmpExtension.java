package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsakmpExtension {

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
