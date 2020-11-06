package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.PptpAndL2tpExtension;
import lombok.Getter;
import lombok.Setter;

/**
 * 这个类独有的字段还不知道什么数据格式  和意思  暂定是以下的这些
 */
@Getter
@Setter
public class Pptpandl2tpData extends AbstractSrcData {

    PptpAndL2tpExtension pptpAndL2tpExtension;

    @Override
    public void adjust() {
        super.adjust();
    }


    @Override
    public String toCsv(char splitChar) {
        String extensionElements = null;
        String extension = null;
        if (null != this.pptpAndL2tpExtension) {
            extensionElements = this.pptpAndL2tpExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.pptpAndL2tpExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
