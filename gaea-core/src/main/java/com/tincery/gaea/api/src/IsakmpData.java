package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.IsakmpExtension;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsakmpData extends AbstractSrcData {

    private IsakmpExtension isakmpExtension;

    @Override
    public void adjust() {
        super.adjust();
        if (this.dataType  == 1){
            isakmpExtension.adjust(true, this.protocol, this.serverPort);
        }
    }

    @Override
    public String toCsv(char splitChar) {
        JSONObject extension = null;
        if (null != this.isakmpExtension) {
            extension = (JSONObject) JSONObject.toJSON(this.isakmpExtension);
            JSONObject jsonObject = this.isakmpExtension.getExtension();
            if (null != jsonObject) {
                extension.putAll(jsonObject);
            }
        }
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
