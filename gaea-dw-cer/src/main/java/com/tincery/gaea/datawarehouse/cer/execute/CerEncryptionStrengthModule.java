package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
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
        try {
            String soPath = NodeInfo.getConfig() + "/rsacheck/libRsaCheck.so";
            System.load(soPath);
//            System.load("C:\\Users\\Insomnia\\Documents\\Visual Studio 2010\\Projects\\helloTest\\x64\\Debug\\helloTest.dll");
            while (true) {
                CerData cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
                if (cer != null) {
                    String nStr = cer.getRsaN();
                    String eStr = cer.getRsaE();
                    if(nStr.length() > 0 && eStr.length() > 0) {
                        try {
                            if(primalityDetect(nStr, eStr)) {
                                cer.setPrimalityDetectResult(1);
                            } else {
                                cer.setPrimalityDetectResult(0);
                            }
                            if(smallFactorDetect(nStr, eStr)) {
                                cer.setSmallFactorDetectResult(1);
                            } else {
                                cer.setSmallFactorDetectResult(0);
                            }
                            if(randomPrimeFactorDetect(nStr, eStr)) {
                                cer.setRandomPrimeFactorDetectResult(1);
                            } else {
                                cer.setRandomPrimeFactorDetectResult(0);
                            }
                            if(rsaFixedPointNumberDetect(nStr, eStr)) {
                                cer.setRsaFixedPointNumberDetectResult(1);
                            } else {
                                cer.setRsaFixedPointNumberDetectResult(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    queueOutput.put(cer);
                }
                if (queueInput.isEnd()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        queueOutput.detach();
        System.out.println("CerEncryptionStrengthModule ends");
    }

    public native boolean primalityDetect(String nStr, String eStr);
    public native boolean smallFactorDetect(String nStr, String eStr);
    public native boolean randomPrimeFactorDetect(String nStr, String eStr);
    public native boolean rsaFixedPointNumberDetect(String nStr, String eStr);
    public native boolean lowIndexAttackDetect(String nStr, String eStr);
}
