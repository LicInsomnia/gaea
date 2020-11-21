package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.SshExtension;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Setter
@Getter
public class SshData extends AbstractSrcData {

    private SshExtension sshExtension;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        String extensionElements = null;
        String extension = null;
        if (null != this.sshExtension) {
            extensionElements = this.sshExtension.toCsv(splitChar);
            extension = JSONObject.toJSONString(this.sshExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extensionElements, extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
