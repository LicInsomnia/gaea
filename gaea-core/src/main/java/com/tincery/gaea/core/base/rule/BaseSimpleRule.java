package com.tincery.gaea.core.base.rule;

import com.tincery.gaea.api.src.AbstractSrcData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class BaseSimpleRule implements Rule {


    protected List<AbstractRuleChecker> ruleCheckers = new ArrayList<>();

    protected boolean activity;

    @Override
    public boolean isActivity() {
        return this.activity;
    }

    @Override
    public boolean matchOrStop(AbstractSrcData abstractSrcData) {
        for (AbstractRuleChecker abstractRuleChecker : this.ruleCheckers) {
            if (abstractRuleChecker.checkAndStop(abstractSrcData)) {
                return true;
            }
        }
        return false;
    }
}
