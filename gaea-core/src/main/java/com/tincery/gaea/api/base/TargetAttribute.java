package com.tincery.gaea.api.base;

import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class TargetAttribute {

    /**
     * 数据中会话时间
     */
    private LocalDateTime capTime;
    /**
     * 数据中会话重点目标名称
     */
    private String targetName;
    /**
     * 数据中会话userId
     */
    private String userId;
    /**
     * 信息数据来源（http分析：网页数据；qq：QQ数据；wechat：WeChat数据）
     */
    private String source;
    /**
     * 信息数据标题（http分析：会话中host；qq：null；wechat：null）
     */
    private String title;
    /**
     * 信息数据url（http分析：会话中url_root；qq：null；wechat：null）
     */
    private String urlRoot;
    /**
     * 提取的信息键值对
     */
    private Set<InformationNode> information;

    public String getKey() {
        return ToolUtils.getMD5(this.targetName + this.userId + this.source + this.title + this.urlRoot + this.information.toString());
    }

}
