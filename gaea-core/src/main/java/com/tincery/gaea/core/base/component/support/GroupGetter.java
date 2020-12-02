package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.ImpTargetSetupDO;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GroupGetter implements InitializationRequired {

    private final ImpTargetSetupDao impTargetSetupDao;
    private final Map<String, String> target2Group = new HashMap<>();

    public GroupGetter(ImpTargetSetupDao impTargetSetupDao) {
        this.impTargetSetupDao = impTargetSetupDao;
    }

    public String getGroupName(String targetName) {
        if (StringUtils.isEmpty(targetName)) {
            return null;
        }
        return this.target2Group.getOrDefault(targetName, "imptarget");
    }

    @Override
    public void init() {
        List<ImpTargetSetupDO> activityData = this.impTargetSetupDao.getActivityData();
        activityData.stream()
                .filter(impTargetSetupDO -> StringUtils.notAllowNull(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()))
                .forEach((impTargetSetupDO) -> this.target2Group.put(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()));
        log.info("加载了{}组目标配置", this.target2Group.size());
    }

}
