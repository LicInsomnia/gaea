package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.base.GaeaData;
import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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
@Slf4j
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


    /****
     * 匹配和终止
     * 此方法中可以写逻辑  也可以控制匹配规则是否终止
     * @author gxz
     * @return 返回值标识是否终止匹配
     * true: 表示终止匹配   false：表示此规则过滤之后继续执行之后的逻辑
     **/

    public boolean checkAndStop(AbstractSrcData data) {
        Class<?> clazz = data.getClass();
        String targetName = data.getTargetName();
        // 获取targetName 属性 如果符合条件直接返回
        if (null == targetName && this.range.equals(1)) {
            return false;
        }
        // 将所有属性放到set中 最后按配置的规则匹配
        Set<Field> fields = new HashSet<>();
        while (clazz != GaeaData.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        String matchValue = null;
        if (this.matchField.contains(".")) {
            // 如果包含 。 说明是级联嵌套内容 需要另外的查询方式
            String[] split = this.matchField.split("\\.");
            String rooFieldName = split[0];
            Optional<Field> rootFieldOptional =
                    fields.stream().filter(field -> field.getName().equals(rooFieldName)).findFirst();
            if (rootFieldOptional.isPresent()) {
                Field rootField = rootFieldOptional.get();
                rootField.setAccessible(true);
                Object rootValue = null;
                try {
                    rootValue = rootField.get(data);
                    if (rootValue == null) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < split.length; i++) {
                    rootValue = getDepthValue(rootValue, split[i]);
                    if (rootValue == null) {
                        return false;
                    }
                }
                matchValue = rootValue.toString();
            } else {
                return false;
            }


        } else {
            // 如果不包含点 直接判断是否存在即可
            Optional<Field> matchFieldOptional =
                    fields.stream().filter(field -> field.getName().equals(this.matchField)).findFirst();
            if (matchFieldOptional.isPresent()) {
                Field matchField = matchFieldOptional.get();
                matchField.setAccessible(true);
                Object value = null;
                try {
                    value = matchField.get(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value == null) {
                    return false;
                }
                matchValue = value.toString();
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

    public Object getDepthValue(Object rootValue, String fieldName) {
        if (rootValue instanceof Map) {
            return ((Map) rootValue).get(fieldName);
        } else {
            Field[] declaredFields = rootValue.getClass().getDeclaredFields();
            Optional<Field> matchField =
                    Arrays.stream(declaredFields).filter(field -> field.getName().equals(fieldName)).findFirst();
            if (matchField.isPresent()) {
                Field field = matchField.get();
                field.setAccessible(true);
                try {
                    return field.get(rootValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

    }


}
