package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.DgaCheck;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuming
 */

@Component
@Slf4j
public class WebCheck implements InitializationRequired {

    private DgaCheck dgaPinyin;
    private DgaCheck dgaEng;
    private DgaCheck dgaAlexa;
    private RandomSubSpace classifyMeta;
    private boolean isInitiated = false;

    @Override
    public void init() {
        try {
            String pathPinyin = NodeInfo.getConfig() + "/webcheck/pinyin.model";
            String pathEng = NodeInfo.getConfig() + "/webcheck/eng.model";
            String pathAlexa = NodeInfo.getConfig() + "/webcheck/alexa.model";
            dgaPinyin = dgaInit(pathPinyin);
            dgaEng = dgaInit(pathEng);
            dgaAlexa = dgaInit(pathAlexa);
            classifyMeta = (RandomSubSpace) SerializationHelper.read(NodeInfo.getConfig() + "/webcheck/rss.model");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        isInitiated = true;
        return;
    }

    public Boolean dgaCheck(String url) {
        try {
            if(!isInitiated) {
                System.err.println("Has not initiated.");
                return false;
            }
            String domain = getTopLevelDomain(url);
            if(domain.equals("")) {
                return false;
            }
            boolean resultPinyi  = dgaPinyin.check(domain);
            boolean resultEng = dgaEng.check(domain);
            boolean resultAlexa = dgaAlexa.check(domain);
            return resultPinyi && resultEng && resultAlexa;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, ArrayList<Double>> dgaCheckByDistribution(String url) {
        if(!isInitiated) {
            System.err.println("Has not initiated.");
            return null;
        }
        try {
            ArrayList<Double> resultList = new ArrayList<>();
            ArrayList<Double> threshList = new ArrayList<>();
            Map<String, ArrayList<Double>> checkMap = new HashMap<>();
            String domain = getTopLevelDomain(url);
            if(domain.equals("")) {
                return null;
            }
            resultList.add(dgaPinyin.checkByDistribution(domain));
            threshList.add(dgaPinyin.getThresh());
            resultList.add(dgaEng.checkByDistribution(domain));
            threshList.add(dgaEng.getThresh());
            resultList.add(dgaAlexa.checkByDistribution(domain));
            threshList.add(dgaAlexa.getThresh());
            checkMap.put("result", resultList);
            checkMap.put("thresh", threshList);
            return checkMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Double> classifyByDistribution(Double l4ProtoValue, Double numPktsSntValue, Double numPktsRcvdValue, Double numBytesSntValue, Double numBytesRcvdValue) {
        try {
            ArrayList<Attribute> attrList = new ArrayList<>();
            Attribute l4Proto = new Attribute("l4Proto");
            Attribute numPktsSnt = new Attribute("numPktsSnt");
            Attribute numPktsRcvd = new Attribute("numPktsRcvd");
            Attribute numBytesSnt = new Attribute("numBytesSnt");
            Attribute numBytesRcvd = new Attribute("numBytesRcvd");
            List<String> typeValues = new ArrayList<>(3);
            typeValues.add("gambling");
            typeValues.add("porn");
            typeValues.add("pure");
            Attribute type = new Attribute("type", typeValues);
            attrList.add(l4Proto);
            attrList.add(numPktsSnt);
            attrList.add(numPktsRcvd);
            attrList.add(numBytesSnt);
            attrList.add(numBytesRcvd);
            attrList.add(type);
            Instances instances = new Instances("dataset", attrList, 0);
            if (instances.classIndex() == -1) {
                instances.setClassIndex(instances.numAttributes() - 1);
            }
            Instance inst = new DenseInstance(6);
            inst.setValue(0, l4ProtoValue);
            inst.setValue(1, numPktsSntValue);
            inst.setValue(2, numPktsRcvdValue);
            inst.setValue(3, numBytesSntValue);
            inst.setValue(4, numBytesRcvdValue);
            inst.setMissing(5);
            inst.setDataset(instances);
            double[] cls =  classifyMeta.distributionForInstance(inst);
            Map<String, Double> result = new HashMap<>();
            result.put("gambling", cls[0]);
            result.put("porn", cls[1]);
            result.put("pure", cls[2]);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double classify(Double l4ProtoValue, Double numPktsSntValue, Double numPktsRcvdValue, Double numBytesSntValue, Double numBytesRcvdValue) {
        try {
            ArrayList<Attribute> attrList = new ArrayList<>();
            Attribute l4Proto = new Attribute("l4Proto");
            Attribute numPktsSnt = new Attribute("numPktsSnt");
            Attribute numPktsRcvd = new Attribute("numPktsRcvd");
            Attribute numBytesSnt = new Attribute("numBytesSnt");
            Attribute numBytesRcvd = new Attribute("numBytesRcvd");
            List typeValues = new ArrayList(3);
            typeValues.add("gambling");
            typeValues.add("porn");
            typeValues.add("pure");
            Attribute type = new Attribute("type", typeValues);
            attrList.add(l4Proto);
            attrList.add(numPktsSnt);
            attrList.add(numPktsRcvd);
            attrList.add(numBytesSnt);
            attrList.add(numBytesRcvd);
            attrList.add(type);
            Instances instances = new Instances("dataset", attrList, 0);
            if (instances.classIndex() == -1) {
                instances.setClassIndex(instances.numAttributes() - 1);
            }
            Instance inst = new DenseInstance(6);
            inst.setValue(0, l4ProtoValue);
            inst.setValue(1, numPktsSntValue);
            inst.setValue(2, numPktsRcvdValue);
            inst.setValue(3, numBytesSntValue);
            inst.setValue(4, numBytesRcvdValue);
            inst.setMissing(5);
            inst.setDataset(instances);
            return classifyMeta.classifyInstance(inst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getClassList() {
        return new ArrayList<>(Arrays.asList("gambling", "porn", "pure"));
    }

    private DgaCheck dgaInit(String modelPath) {
        try {
            DgaCheck dga = new DgaCheck();
            dga.load(modelPath);
            return dga;
//            dgaCheck.load(modelPath);
//            return dgaCheck;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTopLevelDomain(String url) {
        try {
            Pattern pattern = Pattern.compile("[\\w-]+\\.(com|net|org|gov|cc|biz|info|cn|co|vip|me|link|xyz|ru|uk)\\b(\\.(cn|hk|uk|jp|tw))*");
            Matcher matcher = pattern.matcher(url);
            if(matcher.find()) {
                String domain = matcher.group();
                return domain.split("\\.")[0];
            } else {
                return  "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
