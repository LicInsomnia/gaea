package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.SessionExtension;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class SessionData extends AbstractSrcData {

    SessionExtension sessionExtension = new SessionExtension();

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.getDuration(),
                this.getSyn(), this.getFin(),
                this.sessionExtension.toCsv(splitChar),
                JSONObject.toJSONString(this.sessionExtension)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
