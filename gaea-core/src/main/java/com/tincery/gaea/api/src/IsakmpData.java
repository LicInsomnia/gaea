package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.IsakmpExtension;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class IsakmpData extends AbstractSrcData {

    private IsakmpExtension isakmpExtension;

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.getDuration(), this.getSyn(), this.getFin(),
                this.malformedUpPayload, this.malformedDownPayload,
                Objects.isNull(this.isakmpExtension)? null : this.isakmpExtension.toCsv(splitChar),
                Objects.isNull(this.isakmpExtension)? null : JSONObject.toJSONString(this.isakmpExtension.getExtension())
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
