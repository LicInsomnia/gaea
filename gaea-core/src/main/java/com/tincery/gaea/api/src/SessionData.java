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

    SessionExtension sessionExtension;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        String extensionElements = null;
        String extension = null;
        if (null != this.sessionExtension) {
            extensionElements = this.sessionExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.sessionExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
