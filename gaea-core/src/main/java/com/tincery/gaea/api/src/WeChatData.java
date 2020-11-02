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
public class WeChatData extends AbstractSrcData {

    /**
     * 微信私有属性
     */
    private String wxNum;
    private String version;
    private String osType;


    @Override
    public void adjust() {
        super.adjustUserId();
        super.adjustServerId();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar), this.wxNum,this.version,this.osType
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
