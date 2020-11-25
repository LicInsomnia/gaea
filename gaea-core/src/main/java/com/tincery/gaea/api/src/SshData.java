package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.SshExtension;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

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
        fixSshExtension();
    }
    public void fixSshExtension(){
        if (Objects.isNull(this.sshExtension)){
            return;
        }
        List<String> messageList = this.sshExtension.getMessageList();
        if (CollectionUtils.isEmpty(messageList)){
            this.sshExtension.setMessageList(null);
        }
        String clientProtocol = this.sshExtension.getClientProtocol();
        if (StringUtils.isEmpty(clientProtocol)){
            this.sshExtension.setClientProtocol(null);
        }
        String serverProtocol = this.sshExtension.getServerProtocol();
        if (StringUtils.isEmpty(serverProtocol)){
            this.sshExtension.setServerProtocol(null);
        }
    }

    @Override
    public String toCsv(char splitChar) {
        String extension = null;
        if (null != this.sshExtension) {
            extension = JSONObject.toJSONString(this.sshExtension);
        }
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
