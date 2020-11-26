package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CerEncryptionStrengthModule extends BaseModule implements BaseModuleInterface {
    public CerEncryptionStrengthModule() {
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
        System.out.println("CerEncryptionStrengthModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
        while (true) {
            CerData cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
            if (cer != null) {
                CerEncryptionStrengthUtils cerUtils = new CerEncryptionStrengthUtils(cer);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                queueOutput.put(cer);
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        queueOutput.detach();
        System.out.println("CerEncryptionStrengthModule ends");
    }

    public native void hello();
}
