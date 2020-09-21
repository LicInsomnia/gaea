package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;

import java.lang.reflect.Field;
import java.util.Map;

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


    public AbstractRuleChecker(SrcRuleDO srcRuleDO){
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

    public  boolean checkAndStop(AbstractSrcData data){
        Class<?> clazz = data.getClass();
        // 通过反射检测 效率远远大于通过jsonObject
        try {
            Field targetNameField = clazz.getDeclaredField("targetName");
            targetNameField.setAccessible(true);
            targetNameField.get(data);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
                if(this.range.equals(1)){
                    return false;
                }
        }
        try {
            Field matchField = clazz.getDeclaredField(this.matchField);
            matchField.setAccessible(true);
            String matchValue = matchField.get(data).toString().toLowerCase();
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 如果不含有告警规则内的属性 直接返回
             return false;
        }
    }





}
