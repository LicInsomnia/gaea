package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 *
 */
@Setter
@Getter
public class SnmpData extends AbstractSrcData {

    private String version;
    private String community;
    private String pduType;

    @Override
    public void adjust() {
        super.adjustUserId();
        super.adjustServerId();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar),this.version,this.community,this.pduType
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }
}
