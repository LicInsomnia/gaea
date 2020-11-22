package com.tincery.gaea.api.src.email;

import com.tincery.gaea.api.base.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 邮件拓展信息
 */
@Getter
@Setter
public class EmailExtension {

    private Location clientLocation;

    private Location serverLocation;
    /*是否已读 默认为false*/
    private Boolean isRead = false;
    /*是否已读 默认为false*/
    private Boolean isTrash = false;
    /*语种 list<map>*/
    private List<Map<Object,Object>> language;
    /*语种描述*/
    private String languageDesc;

    /*	邮箱域名标签*/
    private String domainTag;
    /*ID 由程序生成，用于数据去重*/
    private String _id;
}
