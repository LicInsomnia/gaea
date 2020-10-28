package com.tincery.gaea.api.dm;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class AssetCondition extends SimpleBaseDO {

    private static final int EQUALS = 1;
    private static final int NO_EQUALS = 2;
    private static final int CONTAIN = 3;
    private static final int NO_CONTAIN = 4;
    private static final int GT = 5;
    private static final int GTE = 6;
    private static final int LT = 7;
    private static final int LTE = 8;
    private static final int AFTER = 9;
    private static final int BEFORE = 10;
    private static final int TRUE = 11;
    private static final int FALSE = 12;
    private static final int EXIST = 13;
    private static final int NO_EXIST = 14;

    private static final int INT = 1;
    private static final int LONG = 2;
    private static final int DOUBLE = 3;
    private static final int STRING = 4;
    private static final int DATE = 5;
    private static final int BOOLEAN = 6;
    private static final int ARRAY = 7;


    @Id
    private String id;

    private String proname;

    private String description;

    private List<ConditionGroup> conditionGroup;


    @Setter
    @Getter
    public static class ConditionGroup {
        List<FieldCondition> conditions;
        List<String> certLinks;
    }


}
