package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;

import java.util.ArrayList;
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
            String soPath = NodeInfo.getConfig() + "/pubkeyCheck/libRsaCheck.so";
            System.load(soPath);
//            System.load("C:\\Users\\Insomnia\\Documents\\Visual Studio 2010\\Projects\\helloTest\\x64\\Debug\\helloTest.dll");
            while (true) {
                CerData cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
                if (cer != null) {
                    String nStr = cer.getRsaN();
                    String eStr = cer.getRsaE();
                    if(nStr.length() > 0 && eStr.length() > 0) {
                        try {
                            List<Integer> resultList = new ArrayList<>();
                            cer.setPrimalityDetectResult(primalityDetect(nStr, eStr) ? 1 : 0);
                            resultList.add(cer.getPrimalityDetectResult());
                            cer.setSmallFactorDetectResult(smallFactorDetect(nStr, eStr) ? 1 : 0);
                            resultList.add(cer.getSmallFactorDetectResult());
                            cer.setRandomPrimeFactorDetectResult(randomPrimeFactorDetect(nStr, eStr) ? 1 : 0);
                            resultList.add(cer.getRandomPrimeFactorDetectResult());
                            cer.setRsaFixedPointNumberDetectResult(rsaFixedPointNumberDetect(nStr, eStr) ? 1 : 0);
                            resultList.add(cer.getRsaFixedPointNumberDetectResult());
                            cer.setLowIndexAttackDetectResult(lowIndexAttackDetect(nStr, eStr) ? 1 : 0);
                            resultList.add(cer.getLowIndexAttackDetectResult());
                            String datPath = NodeInfo.getConfig() + "/pubkeyCheck/";
                            cer.setKeyExchangeLeakDetectResult(keyExchangeLeakDetect(nStr, eStr, datPath) ? 1 : 0);

                            boolean negFlag = false;
                            boolean zeroFlag = false;
                            for(Integer result : resultList) {
                                if(result.equals(0)) {
                                    zeroFlag = true;
                                }
                                if(result.equals(-1)) {
                                    negFlag = true;
                                }
                            }
                            if(negFlag) {
                                cer.setRsaSecurityStatus(-1);
                            } else if(zeroFlag) {
                                cer.setRsaSecurityStatus(0);
                            } else {
                                cer.setRsaSecurityStatus(1);
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
    public native boolean keyExchangeLeakDetect(String xStr, String yStr, String datPath);
}
