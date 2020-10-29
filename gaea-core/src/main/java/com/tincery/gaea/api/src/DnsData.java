package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.DnsExtension;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class DnsData extends AbstractSrcData {

    DnsExtension dnsExtension;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                this.dnsExtension.toCsv(splitChar),
                JSONObject.toJSONString(this.dnsExtension)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
