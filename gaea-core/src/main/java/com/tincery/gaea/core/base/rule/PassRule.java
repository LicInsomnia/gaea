package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.core.base.dao.SrcRuleDao;
import com.tincery.starter.base.mgt.NodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gongxuanzhang
 */
@Slf4j
@Component
public class PassRule extends BaseSimpleRule {


    @Autowired
    private SrcRuleDao srcRuleDao;

    /***session 只需要匹配相应value */
    private final Set<String> passValues = new HashSet<>();

    @Override
    public boolean matchOrStop(AbstractSrcData abstractSrcData) {
        if (isSession()) {
            return checkSession(abstractSrcData);
        }
        return super.matchOrStop(abstractSrcData);
    }



    /**
     * 规则初始化
     * 规则类型(session,dns,ssl,isakmp,email,http)
     */
    @Override
    public void init() {
        List<SrcRuleDO> passData = srcRuleDao.getPassData(NodeInfo.getCategory());
        passData.forEach((passRule) -> {
            if (isSession()) {
                setSessionRule(passRule);
            } else {
                setOtherRule(passRule);
            }
        });
        if (this.ruleCheckers.isEmpty() && this.passValues.isEmpty()) {
            log.warn("未能找到pass规则");
        } else {
            log.info("初始化pass规则成功,共加载{}条规则", this.ruleCheckers.size() + this.passValues.size());
            this.activity = true;
        }
    }

    private boolean isSession() {
        return "session".equals(NodeInfo.getCategory());
    }

    private void setOtherRule(SrcRuleDO srcRuleDO) {
        AbstractRuleChecker addRule = new PassRuleChecker(srcRuleDO);
        ruleCheckers.add(addRule);
    }

    private void setSessionRule(SrcRuleDO srcRuleDO) {
        String ruleValue = srcRuleDO.getRuleValue();
        if (ruleValue != null) {
            this.passValues.add(ruleValue);
        }
    }

    private boolean checkSession(AbstractMetaData abstractMetaData) {
        String serverIp = abstractMetaData.getServerIp();
        return serverIp != null && this.passValues.contains(serverIp);
    }


    public static class PassRuleChecker extends AbstractRuleChecker {

        public PassRuleChecker(SrcRuleDO srcRuleDO) {
            super(srcRuleDO);
        }

    }


}
