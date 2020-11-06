package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.FtpAndTelnetExtension;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtpandtelnetData extends AbstractSrcData {

    FtpAndTelnetExtension ftpAndTelnetExtension;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        String extension = null;
        if (null != this.ftpAndTelnetExtension) {
            extension = JSONObject.toJSONString(this.ftpAndTelnetExtension);
        }
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }
}
