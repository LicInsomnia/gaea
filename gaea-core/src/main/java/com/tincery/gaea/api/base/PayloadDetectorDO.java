package com.tincery.gaea.api.base;


import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class PayloadDetectorDO extends SimpleBaseDO {
    @Id
    private String id;

    @Field("proname")
    private String proName;

    private String description;

    private Rule rule;

    private ApplicationInformationBO application;

    public boolean hasProName() {
        return this.proName != null;
    }

    public boolean hasApplication() {
        return this.application != null;
    }

    public boolean hitUpPayload(String upPayload) {
        return this.rule.hitUpPayload(upPayload);
    }

    public boolean hitPayload(String downPayload, String upPayload) {
        return hitUpPayload(upPayload) && hitDownPayload(downPayload);
    }

    public boolean hitDownPayload(String downPayload) {
        return this.rule.hitDownPayload(downPayload);
    }

    public boolean hasProtocolAndPort() {
        return this.rule.protocol != null && (this.rule.clientPort != null || this.rule.serverPort != null);
    }

    public boolean dontHaveProtocolAndPort() {
        return !hasProtocolAndPort();
    }

    public String getGroupKey() {
        Integer port = this.rule.clientPort == null ? this.rule.serverPort : this.rule.clientPort;
        return this.rule.protocol + "_" + port;
    }

    public boolean hitExtension(Map<String, Object> extension) {
        return this.rule.hitExtension(extension);
    }

    @Setter
    @Getter
    public static class Rule {

        @Field("up_index")
        private int upIndex;

        @Field("down_index")
        private int downIndex;

        @Field("up_payload")
        private String upPayload;

        @Field("down_payload")
        private String downPayload;

        private Integer protocol;

        @Field("serverport")
        private Integer serverPort;

        @Field("clientport")
        private Integer clientPort;

        private List<KV<String,Object>> extension;

        /****
         * 匹配载荷内容 如果规则中没有 则直接通过
         * @author gxz
         * @param upPayload 上行载荷内容
         * @return boolean
         **/
        private boolean hitUpPayload(String upPayload) {
            if (StringUtils.isEmpty(this.upPayload)) {
                return true;
            }
            if (StringUtils.isEmpty(upPayload) || upPayload.length() < this.upIndex) {
                return false;
            }
            return upPayload.substring(this.upIndex).startsWith(this.upPayload);
        }

        private boolean hitDownPayload(String downPayload) {
            if (StringUtils.isEmpty(this.downPayload)) {
                return true;
            }
            if (StringUtils.isEmpty(downPayload) || downPayload.length() < this.downIndex) {
                return false;
            }
            return downPayload.substring(this.downIndex).startsWith(this.downPayload);
        }

        private boolean hitExtension(Map<String, Object> extension) {
            for (KV<String,Object> kv : this.extension) {
                String key = kv.getKey();
                String value = extension.getOrDefault(key, "").toString();
                if (!value.contains(kv.getValue().toString())) {
                    return false;
                }
            }
            return true;
        }


    }

}
