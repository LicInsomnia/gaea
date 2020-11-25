package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liuming
 */
public class CerComplianceModule extends BaseModule implements BaseModuleInterface {

    public CerComplianceModule() {
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
        System.out.println("CerComplianceModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
        while (true) {
            CerData cer = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
            if(cer != null) {
                CerComplianceModuleUtils cerObj = new CerComplianceModuleUtils(cer);
                int complianceType = 0;
                try {
                    complianceType = cerObj.checkVersion() | cerObj.checkSerialNum() | cerObj.checkValidTime() |
                            cerObj.checkPublicKey() | cerObj.checkSignKey() | cerObj.checkSubjectCN();
                    if (cerObj.checkSelfSigned()) {
                        cer.setSelfSigned(1);
                    } else {
                        cer.setSelfSigned(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cer.setCompliance(complianceType == 0 ? 0 : 1);
                cer.setComplianceType(complianceType);
                List<String> list = cerObj.getDetailList();
                cer.setComplianceDetail(list.toArray(new String[list.size()]));
                queueOutput.put(cer);
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        queueOutput.detach();
        System.out.println("CerComplianceModule ends");
    }
}
