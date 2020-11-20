package com.tincery.gaea.source.alarm.execute;

import com.tincery.gaea.api.base.DpdkRuleDO;
import com.tincery.gaea.api.src.AlarmTupleData;
import com.tincery.gaea.api.src.extension.AlarmExtension;
import com.tincery.gaea.core.base.component.support.DpdkRuleDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class AlarmLineSupport extends SrcLineSupport {

    @Autowired
    private DpdkRuleDetector dpdkRuleDetector;

    @Autowired
    private IpSelector ipSelector;

    public AlarmExtension getAlarmExtension(String ruleName, String title) {
        DpdkRuleDO dpdkRule = this.dpdkRuleDetector.getDpdkRule(ruleName);
        if (null == dpdkRule) {
            return null;
        }
        AlarmExtension alarmExtension = new AlarmExtension();
        alarmExtension.setOrgLink(dpdkRule.getOrgLink());
        alarmExtension.setIsSystem(dpdkRule.getIsSystem());
        alarmExtension.setType(dpdkRule.getType());
        alarmExtension.setRuleName(dpdkRule.getRuleName());
        alarmExtension.setCreateUser(dpdkRule.getCreateUser());
        alarmExtension.setViewUsers(new HashSet<>(dpdkRule.getViewUsers()));
        alarmExtension.setCategory(13);
        alarmExtension.setSubCategory("5Tuple");
        alarmExtension.setCategoryDesc(dpdkRule.getCategory());
        alarmExtension.setSubCategoryDesc(dpdkRule.getSubcategory());
        alarmExtension.setTitle(title);
        alarmExtension.setLevel(dpdkRule.getLevel());
        alarmExtension.setTask(dpdkRule.getTask());
        alarmExtension.setRemark(dpdkRule.getRemark());
        alarmExtension.setCheckMode(dpdkRule.getMode());
        alarmExtension.setAccuracy(1);
        alarmExtension.setPublisher(dpdkRule.getPublisher());
        return alarmExtension;
    }

    public void setLocation(AlarmTupleData alarmTupleData) {
        alarmTupleData.setClientLocation(this.ipSelector.getCommonInformation(alarmTupleData.getClientIp()));
        alarmTupleData.setServerLocation(this.ipSelector.getCommonInformation(alarmTupleData.getServerIp()));
        alarmTupleData.setClientLocationOuter(this.ipSelector.getCommonInformation(alarmTupleData.getClientIpOuter()));
        alarmTupleData.setServerLocationOuter(this.ipSelector.getCommonInformation(alarmTupleData.getServerIpOuter()));
    }

}
