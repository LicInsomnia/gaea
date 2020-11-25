package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MarkModule extends BaseModule implements BaseModuleInterface {
    public MarkModule() {
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
        System.out.println("MarkModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
        CerMark cerMark = new CerMark();
        while (true) {
            //System.out.println(queueInput.getTag() + queueInput.size());
            CerData doc = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
            if(doc != null) {
                try {
                    cerMark.setTags(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                queueOutput.put(doc);
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        queueOutput.detach();
        System.out.println("MarkModule ends");
    }

    class CerMark{

        List<String> stateSecretList = new ArrayList<>(Arrays.asList("1.2.840.10045.2.1", "1.2.156.10197.1.301", "1.2.156.10197.1.501", "1.2.156.10197.1.502", "1.2.156.10197.1.503", "1.2.156.10197.1.504"));

        public Set<String> checkCase(CerData doc) {
            //Set<String> caseTags = this.markRuleUtils.check(doc);
            Set<String> caseTags = new HashSet<>();
            String subjectPublicKeyAlgoOid = "";
            if (doc.getSubjectPublicKeyInfoAlgorithmOid() != null) {
                subjectPublicKeyAlgoOid = doc.getSubjectPublicKeyInfoAlgorithmOid();
            }
            String signatureAlgooid = "";
            if (doc.getSignatureAlgorithmOid() != null) {
                signatureAlgooid = doc.getSignatureAlgorithmOid();
            }
            if (this.stateSecretList.contains(subjectPublicKeyAlgoOid) || this.stateSecretList.contains(signatureAlgooid)) {
                caseTags.add("国密");
            }
            return caseTags;
        }

        public Set<String> checkReliability(CerData doc) {
            List<Double> threshold = new ArrayList<>(((Map<Integer, Double>)Config.cerProperties.getDefaultConfig().get("reliabilityThreshold")).values());
            Set<String> tags = new HashSet<>();
            if(threshold == null || threshold.size() == 0) {
                return new HashSet<>();
            }
            double reliability = doc.getReliability();
            if(reliability < threshold.get(1)) {
                tags.add("不可信");
            } else if(reliability > threshold.get(0)) {
                tags.add("可信");
            } else {
                tags.add("待证实");
            }
            return tags;
        }
        public Set<String> checkCompliance(CerData doc) {
            Set<String> tags = new HashSet<>();
            Integer complianceType = doc.getComplianceType();
            for(int i=0;i<CerComplianceModuleUtils.typeNum;i++) {
                int result = complianceType & (1 << i);
                if(result != 0) {
                    tags.add(CerComplianceModuleUtils.descriptionList.get(i));
                }
            }
            return tags;
        }
        public Set<String> checkGmCompliance(CerData doc) {
            Set<String> tags = new HashSet<>();
            Integer gmcomplianceType = doc.getGmcomplianceType();
            for(int i=0;i<CerGmComplianceModuleUtils.typeNum;i++) {
                int result = gmcomplianceType & (1 << i);
                if(result != 0) {
                    tags.add(CerGmComplianceModuleUtils.descriptionList.get(i));
                }
            }
            return tags;
        }

        public CerData setTags(CerData doc) {
            Set<String> caseTags = checkCase(doc);
            doc.setCaseTags(caseTags);
            Set<String> complianceTags = checkCompliance(doc);
            doc.setComplianceTags(complianceTags.toArray(new String[complianceTags.size()]));
            Set<String> reliabilityTags = checkReliability(doc);
            doc.setReliabilityTags(reliabilityTags.toArray(new String[reliabilityTags.size()]));
            Set<String> gmcomplianceTags = checkGmCompliance(doc);
            doc.setGmcomplianceTags(gmcomplianceTags.toArray(new String[gmcomplianceTags.size()]));
            return doc;
        }
    }
}
