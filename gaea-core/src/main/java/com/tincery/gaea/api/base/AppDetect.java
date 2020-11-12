package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class AppDetect extends SimpleBaseDO {
    @Id
    private String id;

    /**条件栈*/
    private List<SearchCondition> conditions;

    /**描述*/
    private String description;

    /**规则*/
    private List<SearchRule> rules;

    /**应用信息*/
    private ApplicationInformationBO appInfo;

    /**告警信息*/
    private AlertInfo alertInfo;

    /**距离时间*/
    private long duration;


    public int getRuleCount(){
        return rules.size();
    }

    public Set<String> getCategorySet(){
        return rules.stream().map(SearchRule::getMatch).map(KV::getKey).collect(Collectors.toSet());
    }



    @Data
    public static class AlertInfo {
        private String category;
        private String subcategory;
        private String categoryDesc;
        private String subcategoryDesc;
        private Integer level;
        private String accuracy;
        private String remark;
    }

}
