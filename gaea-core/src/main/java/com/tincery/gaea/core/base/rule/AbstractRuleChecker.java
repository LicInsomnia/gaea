package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.GaeaData;
import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.api.src.SshData;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author gongxuanzhang
 * 规则的执行者 最终真正执行check方法的类
 * 使用方法参照默认实现
 * 一般作为Rule的内部类使用
 * 一个PassRule 中 加载多个passRuleChecker
 * @see PassRule
 */
public abstract class AbstractRuleChecker {

    protected String matchField;
    protected String ruleValue;
    protected Integer mode;
    protected Integer range;


    public AbstractRuleChecker(SrcRuleDO srcRuleDO) {
        this.matchField = srcRuleDO.getMatchField();
        this.ruleValue = srcRuleDO.getRuleValue();
        this.mode = srcRuleDO.getMode();
        this.range = srcRuleDO.getRange();
    }

    public static void main(String[] args) throws Exception {
        Class<?> clazz = SshData.class;
        Class<?> srcDataClass = SshData.class;
        while (srcDataClass != AbstractMetaData.class) {
            srcDataClass = srcDataClass.getSuperclass();
        }
        Field targetName = srcDataClass.getDeclaredField("targetName");

        System.out.println(targetName);
    }

    /****
     * 匹配和终止
     * 此方法中可以写逻辑  也可以控制匹配规则是否终止
     * @author gxz
     * @return 返回值标识是否终止匹配
     * true: 表示终止匹配   false：表示此规则过滤之后继续执行之后的逻辑
     **/

    public boolean checkAndStop(AbstractSrcData data) {
        Class<?> clazz = data.getClass();
        Class<?> srcDataClass = data.getClass();
        while (srcDataClass != AbstractMetaData.class) {
            srcDataClass = srcDataClass.getSuperclass();
        }
        try {
            // 获取targetName 属性 如果符合条件直接返回
            Field targetNameField = srcDataClass.getDeclaredField("targetName");
            targetNameField.setAccessible(true);
            Object targetName = targetNameField.get(data);
            if (null == targetName && this.range.equals(1)) {
                return false;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // 将所有属性放到set中 最后按配置的规则匹配
        Set<Field> fields = new HashSet<>();
        while (clazz != GaeaData.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        String matchValue = null;
        Optional<Field> matchFieldOptional = fields.stream().filter(field -> field.getName().equals(this.matchField)).findFirst();
        if (matchFieldOptional.isPresent()) {
            Field matchField = matchFieldOptional.get();
            matchField.setAccessible(true);
            try {
                Object matchValueObject = matchField.get(data);
                if (matchValueObject == null) {
                    return false;
                }
                matchValue = matchValueObject.toString().toLowerCase();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (matchValue == null) {
            return false;
        }
        switch (this.mode) {
            case 0:
                return matchValue.equals(this.ruleValue.toLowerCase());
            case 1:
                return matchValue.contains(this.ruleValue.toLowerCase());
            case 2:
                return matchValue.startsWith(this.ruleValue.toLowerCase());
            case 3:
                return matchValue.endsWith(this.ruleValue.toLowerCase());
            default:
                return false;
        }

    }


}
