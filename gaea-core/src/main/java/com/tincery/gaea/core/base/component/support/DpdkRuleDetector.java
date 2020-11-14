package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.DpdkRuleDO;
import com.tincery.gaea.core.base.dao.DpdkRuleDao;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DpdkRuleDetector implements InitializationRequired {

    private final Map<String, DpdkRuleDO> map = new HashMap<>();

    @Autowired
    private DpdkRuleDao dpdkRuleDao;

    public DpdkRuleDO getDpdkRule(String ruleName) {
        return this.map.get(ruleName);
    }

    @Override
    public void init() {
        Query query = new Query();
        query.addCriteria(Criteria.where("activity").is(true).and("function").is(0));
        List<DpdkRuleDO> dpdkRuleList = this.dpdkRuleDao.findList(query).getData();
        for (DpdkRuleDO dpdkRule : dpdkRuleList) {
            String ruleName = dpdkRule.getRuleName();
            map.put(ruleName, dpdkRule);
        }
    }

}
