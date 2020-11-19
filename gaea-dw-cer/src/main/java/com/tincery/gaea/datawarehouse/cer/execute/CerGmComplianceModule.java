package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CerGmComplianceModule extends BaseModule implements BaseModuleInterface {

    public CerGmComplianceModule() {
        super();
    }
    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 1);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 1);
    }

    @Override
    public void run() {
        System.out.println("CerGmComplianceModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
        while (true) {
            CerData cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
            if (cer != null) {
                CerGmComplianceModuleUtils cerUtils = new CerGmComplianceModuleUtils(cer);
                int complianceType = 0;
                try {
                    complianceType = cerUtils.checkIssuer() | cerUtils.checkAlgo() | cerUtils.checkValid() | cerUtils.checkVersion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cer.setGmcompliancetype(complianceType);
                List<String> list = cerUtils.getDetailList();
                cer.setGmcompliancedetail(list.toArray(new String[list.size()]));
                queueOutput.put(cer);
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        queueOutput.detach();
        System.out.println("CerGmComplianceModule ends");
    }
}
