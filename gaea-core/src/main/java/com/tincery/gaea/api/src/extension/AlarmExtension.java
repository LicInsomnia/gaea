package com.tincery.gaea.api.src.extension;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AlarmExtension {

    private String orgLink;
    private Boolean isSystem;
    private Integer type;
    private String ruleName;
    private String createUser;
    private Set<String> viewUsers;
    private Integer category;
    private String categoryDesc;
    private String subCategory;
    private String subCategoryDesc;
    private String title;
    private Integer level;
    private String task;
    private String remark;
    private Integer checkMode;
    private Integer accuracy;
    private String publisher;

}
