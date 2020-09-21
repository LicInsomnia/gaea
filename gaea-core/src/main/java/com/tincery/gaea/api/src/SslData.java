package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SslData extends AbstractSrcData {


    private String serverName;
    private String sha1;
    /**
     * 是否是双向会话
     */
    private boolean doubleSession;
    /**
     * 证书链
     */
    private List<String> cerChain;
    private List<String> clientCerChain;
    /**
     * 随机数
     */
    private String random;
    /**
     * 服务端支持的版本号
     */
    private List<String> versions;
    /**
     * 服务端支持的加密算法
     */
    private List<String> cipherSuites;
    /**
     * 客户端的加密算法
     */
    private String clientCipherSuite;
    /**
     * 握手过程
     */
    private String handshake;

    @Override
    public void adjust() {
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.durationTime, this.syn, this.fin, this.serverName, this.sha1
                , formatList(this.cerChain), formatList(this.clientCerChain), this.doubleSession, this.random, formatList(this.versions)
                , formatList(this.cipherSuites), this.clientCipherSuite, this.handshake, this.malformedUpPayload, this.malformedDownPayload};
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    private String formatList(List<String> list) {
        String result = null;
        if (!CollectionUtils.isEmpty(list)) {
            result = Joiner.on(";").join(list);
        }
        return result;
    }

    public void setHandshake(String handshake) {
        String[] element = handshake.split(" --> ");
        int len = element.length;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String key = element[i];
            stringBuilder.append(key);
            if ("Application Data".equals(key)) {
                int count = 0;
                for (int j = i; j < len; j++, i++) {
                    count++;
                    i = j;
                    if (!"Application Data".equals(element[j])) {
                        break;
                    }
                }
                if (count > 1) {
                    stringBuilder.append(" * ").append(count);
                }
            }
            stringBuilder.append(" --> ");
        }
        stringBuilder.setLength(stringBuilder.length() - 4);
        this.handshake = stringBuilder.toString().trim();
    }

    public void addCert(String cert) {
        if (null == cert || cert.isEmpty()) {
            return;
        }
        if (null == this.sha1) {
            String sha1 = cert.split("_")[0];
            if (!sha1.isEmpty()) {
                this.sha1 = sha1;
            }
        }
        if (null == this.cerChain) {
            this.cerChain = new ArrayList<>();
        }
        this.cerChain.add(cert);
    }


}
