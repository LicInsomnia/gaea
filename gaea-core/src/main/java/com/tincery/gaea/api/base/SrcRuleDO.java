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
public class SrcRuleDO extends SimpleBaseDO implements Serializable, Cloneable {


    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("match_field")
    private String matchField;
    private String subcategory;
    private String category;
    private Boolean activity;
    @Field("rule_name")
    private String ruleName;
    private Integer level;
    @Field("rule_value")
    private String ruleValue;
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
    private Integer accuracy;
    private String publisher;
    @Field("web_flag")
    private Integer webFlag;
    @Field("gaea_flag")
    private Integer gaeaFlag;


    /****权限相关*/
    @Field("org_link")
    private String orgLink;
    @Field("create_user")
    private String createUser;
    private Boolean isSystem;

}

