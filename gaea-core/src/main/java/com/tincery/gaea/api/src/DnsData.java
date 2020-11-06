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
        String extensionElements = null;
        String extension = null;
        if (null != this.dnsExtension) {
            extensionElements = this.dnsExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.dnsExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
