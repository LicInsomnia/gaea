package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class IsakmpExtension {

    static Set<String> initiatorSpecialElementKey = new HashSet<>(Arrays.asList("encryption_algorithm", "hash_algorithm", "group_description"));
    static Set<String> responderSpecialElementKey = new HashSet<>(Arrays.asList("encryption_algorithm", "hash_algorithm", "group_description"));
    String messageListStr = null;
    String initiatorInformationStr = null;
    String responderInformationStr = null;
    String initiatorVidStr = null;
    String responderVidStr = null;
    private List<String> messageList;
    private Set<JSONObject> initiatorInformation;
    private Set<JSONObject> responderInformation;
    private Set<JSONObject> initiatorVid;
    private Set<JSONObject> responderVid;
    private JSONObject extension;

    public void setExtension() {
        this.extension = new JSONObject();
        if (null != this.messageList && !this.messageList.isEmpty()) {
            this.extension.put("message_list", this.messageList);
            this.messageListStr = ToolUtils.convertString(this.messageList, ";");
        }
        if (null != this.initiatorInformation && !this.initiatorInformation.isEmpty()) {
            this.extension.put("initiator_information", this.initiatorInformation);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.initiatorInformation) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.initiatorInformationStr = stringBuilder.toString();
        }
        if (null != this.responderInformation && !this.responderInformation.isEmpty()) {
            this.extension.put("responder_information", this.responderInformation);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.responderInformation) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.responderInformationStr = stringBuilder.toString();
        }
        if (null != this.initiatorVid && !this.initiatorVid.isEmpty()) {
            this.extension.put("initiator_vid", this.initiatorVid);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.initiatorVid) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.initiatorVidStr = stringBuilder.toString();
        }
        if (null != this.responderVid && !this.responderVid.isEmpty()) {
            this.extension.put("responder_vid", this.responderVid);
            StringBuilder stringBuilder = new StringBuilder();
            for (JSONObject jsonObject : this.responderVid) {
                stringBuilder.append(JSONObject.toJSONString(jsonObject)).append(";");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            this.responderVidStr = stringBuilder.toString();
        }
    }

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                messageListStr,
                initiatorInformationStr,
                responderInformationStr,
                initiatorVidStr,
                responderVidStr
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
