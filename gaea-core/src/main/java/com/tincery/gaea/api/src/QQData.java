package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Setter
@Getter
public class QQData extends AbstractSrcData {

    private String qq;

    @Override
    public void adjust() {
        super.adjustUserId();
        super.adjustServerId();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    public JSONObject toJsonObjects() {
        return (JSONObject) JSONObject.toJSON(this);
    }

}
