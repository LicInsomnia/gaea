package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gxz
 */
@Data
public class ImpTargetSetupDO extends SimpleBaseDO {
        @Id
    private String id;
    /**
     * 组名
     **/
    private String groupname;
    /**
     * 目标名称
     **/
    private String targetname;
    /**
     * 添加时间
     **/
    private LocalDateTime starttime;
    /**
     * 创建用户
     **/
    private String createuser;
    /**
     * 信息内容
     **/
    private TargetInfo info;
    /***名称**/
    private String name;
    /***自定义的键值对**/
    @Field("custom_map")
    private List<KV>  customMap;
    /**
     * 最后时间
     **/
    private LocalDateTime lasttime;

    private Boolean activity;

    @Field("is_online")
    private Boolean isOnline;

    private List<String> tag;

    /***插入，编辑后为：1，删除目标除了activity置为false，该字段为-1*/
    private Integer status;

    /**
     * 最后访问时间 是从user_visit表中读取的,用于计算
     **/
    private LocalDateTime visittime;

    private List<String> view_users;

    private String orgLink;

    private Boolean isSystem;

    private Boolean important;


    @Setter
    @Getter
    public static class TargetInfo{
        private String name;
        private List<String> ip;
        private List<String> adsl;
        private List<String> imsi;
    }

}
