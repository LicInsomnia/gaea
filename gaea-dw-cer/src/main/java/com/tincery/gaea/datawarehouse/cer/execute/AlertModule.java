package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import java.util.List;

public class AlertModule extends BaseModule implements BaseModuleInterface {

    public AlertModule() {
        super();
    }

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 1);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 0);
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }

    private void init() {
        CerAlarmRule cerAlarmRule = new CerAlarmRule();
        cerAlarmRule.init();
        registryRules(cerAlarmRule);

    }

    @Override
    public void run() {
        System.out.println("AlertModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        init();
        while (true) {
            if (queueInput.size() != 0) {
                CerData doc = (CerData) queueInput.poll();
                try {
                    RuleRegistry.getInstance().matchLoop(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        AlarmRule.writeAlarm(NodeInfo.getAlarmMaterial(), "", Config.cerProperties.getMaxLine());
        AlarmRule.writeEvent(NodeInfo.getEventData(), "", Config.cerProperties.getMaxLine());
        System.out.println("AlertModule ends");
    }
}
