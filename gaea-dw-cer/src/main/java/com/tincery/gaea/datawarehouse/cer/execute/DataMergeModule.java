package com.tincery.gaea.datawarehouse.cer.execute;

import com.google.common.collect.Sets;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuming
 */
public class DataMergeModule extends BaseModule implements BaseModuleInterface {

    public DataMergeModule() {
        super();
    }

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 4);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 2);
    }

    @Override
    public void run() {
        try{
            System.out.println("DataMergeModule starts.");
            Map<String, CerData> complianceMap = new HashMap<>();
            Map<String, CerData> reliabilityMap = new HashMap<>();
            Map<String, CerData> gmcomplianceMap = new HashMap<>();
            while (true) {
                boolean complianceFlag = false;
                boolean reliabilityFlag = false;
                boolean gmcomplianceFlag = false;
                for (DataQueue queueInput : queuesInput) {
                    //System.out.println(queueInput.getTag() + queueInput.size());
                    CerData doc = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
                    if(doc != null) {
                        switch (queueInput.getTag()) {
                            case "CerComplianceModule->DataMergeModule":
                                complianceMap.put(doc.getId(), doc);
                                break;
                            case "CerReliabilityModule->DataMergeModule":
                                reliabilityMap.put(doc.getId(), doc);
                                break;
                            case "CerGmComplianceModule->DataMergeModule":
                                gmcomplianceMap.put(doc.getId(), doc);
                                break;
                        }
                    }
                    if (queueInput.isEnd()) {
                        switch (queueInput.getTag()) {
                            case "CerComplianceModule->DataMergeModule":
                                complianceFlag = true;
                                break;
                            case "CerReliabilityModule->DataMergeModule":
                                reliabilityFlag = true;
                                break;
                            case "CerGmComplianceModule->DataMergeModule":
                                gmcomplianceFlag = true;
                                break;
                        }
                    }
                }
                Set<String> complianceKeySet = complianceMap.keySet();
                Set<String> reliabilityKeySet = reliabilityMap.keySet();
                Set<String> gmcomplianceKeySet = gmcomplianceMap.keySet();
                Set<String> commonKeySet = new HashSet<>(Sets.intersection(gmcomplianceKeySet, Sets.intersection(complianceKeySet, reliabilityKeySet)));
                for (String key : commonKeySet) {
                    CerData result = complianceMap.get(key);
//                    CertDo reliabilityDoc = reliabilityMap.get(key);
//                    CertDo gmcomplianceDoc = gmcomplianceMap.get(key);
//                    result.setReliability(reliabilityDoc.getReliability());
//                    result.setReliabilitytype(reliabilityDoc.getReliabilitytype());
//                    result.setReliabilitydetail(reliabilityDoc.getReliabilitydetail());
//                    result.setAltnamenum(reliabilityDoc.getAltnamenum());
//                    result.put("altnamewhitenum", reliabilityDoc.get("altnamewhitenum"));
//                    result.put("altnamedganum", reliabilityDoc.get("altnamedganum"));
//                    result.put("malicious_website", reliabilityDoc.get("malicious_website"));
//                    result.put("gmcompliancetype", gmcomplianceDoc.get("gmcompliancetype"));
//                    result.put("gmcompliancedetail", gmcomplianceDoc.get("gmcompliancedetail"));
                    complianceMap.remove(key);
                    reliabilityMap.remove(key);
                    gmcomplianceMap.remove(key);
                    for (DataQueue queueOutput : queuesOutput) {
                        queueOutput.put(result);
                    }
                }
                if (complianceFlag && reliabilityFlag && gmcomplianceFlag) {
                    break;
                }
            }
            for (DataQueue queueOutput : queuesOutput) {
                queueOutput.detach();
            }
            System.out.println("DataMergeModule ends");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
