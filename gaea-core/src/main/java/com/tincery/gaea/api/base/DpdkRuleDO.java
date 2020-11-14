package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 规则
 *
 * @author gxz
 */
@Setter
@Getter
@ToString
public class DpdkRuleDO extends SimpleBaseDO implements Serializable, Cloneable {


    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("server_ip")
    private String serverIp;
    @Field("client_ip")
    private String clientIp;
    private Integer protocol;
    @Field("client_port")
    private Integer clientPort;
    @Field("serverPort")
    private Integer serverPort;
    private Integer status;
    private String subcategory;
    private String category;
    private Boolean activity;
    @Field("rule_name")
    private String ruleName;
    private Integer level;
    private Integer mode;
    private Integer function;
    private String task;
    private Integer type;
    private Integer range;
    private String remark;
    @Field("start_time")
    private LocalDateTime startTime;
    @Field("view_users")
    private List<String> viewUsers;
    @Field("case_tags")
    private List<String> caseTags;
    private String publisher;
    /****权限相关*/
    @Field("org_link")
    private String orgLink;
    @Field("create_user")
    private String createUser;
    private Boolean isSystem;

}

