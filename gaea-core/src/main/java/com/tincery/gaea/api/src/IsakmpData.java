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
            isakmpExtension.adjust(false, this.protocol, this.serverPort);
        }else{
            IsakmpExtension isakmpExtension = new IsakmpExtension();
            isakmpExtension.setProtocolVersion("非标准IPSEC");
            this.isakmpExtension = isakmpExtension;
        }
    }

    @Override
    public String toCsv(char splitChar) {
        String extension = JSONObject.toJSONString(this.isakmpExtension);
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
