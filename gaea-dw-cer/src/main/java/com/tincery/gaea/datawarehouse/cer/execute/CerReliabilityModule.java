package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.support.WebCheck;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import org.bson.Document;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CerReliabilityModule extends BaseModule implements BaseModuleInterface {

    private final Map<String, Object> sha1WithChain = new HashMap<>();//key:证书sha1值;value:证书对应的证书链数据
    private final Map<String, Object> sha1WithRaw = new HashMap<>();//key:证书sha1值;value:证书的base64编码源数据
//    WebCheck webCheck = new WebCheck();

    public CerReliabilityModule() {
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
        System.out.println("CerReliabilityModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        DataQueue queueOutput = queuesOutput.get(0);
//        webCheck.init();
        while (true) {
            CerData cer = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
            if(cer != null) {
                try {
                    CerReliabilityModuleUtils cerObj = new CerReliabilityModuleUtils(cer);
                    Map<String, Object> analysisResult = analysis(cerObj);
                    Double reliabilityScore = Double.valueOf(analysisResult.get("score").toString());
                    Integer reliabilityType = Integer.valueOf(analysisResult.get("type").toString());
                    cer.setReliability(reliabilityScore);
                    cer.setReliabilityType(reliabilityType);
                    List<String> list = cerObj.getDetailList();
                    cer.setReliabilityDetail(list.toArray(new String[list.size()]));
                    ArrayList<String> subjectAltNameList = checkAltName(cer);
                    int altNameNum = subjectAltNameList.size();
                    cer.setAltNameNum(altNameNum);
                    ArrayList<String> subjectAltNameWhiteList = checkAltNameWhite(subjectAltNameList);
                    int altNameWhiteNum = subjectAltNameWhiteList.size();
                    cer.setAltNameWhiteNum(altNameWhiteNum);
                    ArrayList<String> subjectAltNameDgaList = checkAltNameDga(subjectAltNameList);
                    int altNameDgaNum = subjectAltNameDgaList.size();
                    cer.setAltNameDgaNum(altNameDgaNum);
                    cer.setMaliciousWebsite(checkMalicious(reliabilityScore, altNameNum, altNameDgaNum, altNameWhiteNum));
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
        System.out.println("CerReliabilityModule ends");
    }

    public ArrayList<String> checkAltName(CerData doc) {
        ArrayList<String> subjectAltNameList = new ArrayList<>();
        try {
            if(doc.getExSubjectAltName() == null) {
                return new ArrayList<>();
            }
            String subjectAltNameStr = doc.getExSubjectAltName();
            if(subjectAltNameStr.startsWith("[") && subjectAltNameStr.endsWith("]")) {
                subjectAltNameStr = subjectAltNameStr.substring(1, subjectAltNameStr.length() - 1);
            }
            String[] subjectAltNameFullList = subjectAltNameStr.split(",");
            for(String subjectAltName : subjectAltNameFullList) {
                if(subjectAltName.length() >= 8) {
                    subjectAltNameList.add(subjectAltName.substring(8));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjectAltNameList;
    }

    public ArrayList<String> checkAltNameWhite(ArrayList<String> subjectAltNameList) {
        ArrayList<String> subjectAltNameWhiteList = new ArrayList<>();
        try {
            for(String subjectAltName : subjectAltNameList) {
                if(Config.appCheck.getApplicationInformation(subjectAltName) != null) {
                    subjectAltNameWhiteList.add(subjectAltName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjectAltNameWhiteList;
    }

    public ArrayList<String> checkAltNameDga(ArrayList<String> subjectAltNameList) {
        ArrayList<String> subjectAltNameDgaList = new ArrayList<>();
        try {
            for(String subjectAltName : subjectAltNameList) {
                if(Config.webCheck.dgaCheck(subjectAltName)) {
                    subjectAltNameDgaList.add(subjectAltName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjectAltNameDgaList;
    }

    private int checkMalicious(Double reliabilityScore, int altNameNum, int altNameDgaNum, int altNameWhiteNum) {
        for(Document configDoc : Config.cerProperties.getWebcheck()) {
            WebCheckConfig config = new WebCheckConfig();
            config.init(configDoc);
            if(!(reliabilityScore > config.getReliability().get("0") && reliabilityScore <= config.getReliability().get("1"))) {
                continue;
            }
            if(!(altNameNum > config.getAltNameNum().get("0") && altNameNum <= config.getAltNameNum().get("1"))) {
                continue;
            }
            if(!(altNameDgaNum > config.getAltNameDgaNum().get("0") && altNameDgaNum <= config.getAltNameDgaNum().get("1"))) {
                continue;
            }
            if(!(altNameWhiteNum > config.getAltNameWhiteNum().get("0") && altNameWhiteNum <= config.getAltNameWhiteNum().get("1"))) {
                continue;
            }
            return config.getScore();
        }
        return 0;
    }

    private Map<String, Object> analysis(CerReliabilityModuleUtils cerObj) {
        try {
            Double reliabilityScore;
            Integer reliabilityType;
            Map<String, Object> result;
            reliabilityType = 0;
            reliabilityScore = 0D;
            result = cerObj.calcSelfSignedScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcSubContentNumScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcSubCnIsUrlScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcSubCnInFASScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcSubCnInFAASScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcLongValidLenScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcAbnormalActScore();
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            result = cerObj.calcReliableAuthScore(null, null, null);
            reliabilityScore += (Double) result.get("score");
            reliabilityType = reliabilityType | (Integer) result.get("reliabilityType");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("score", reliabilityScore);
            resultMap.put("type", reliabilityType);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    static class WebCheckConfig {
        Map<String, Integer> altNameNum;
        Map<String, Integer> altNameDgaNum;
        Map<String, Integer> altNameWhiteNum;
        Map<String, Double> reliability;
        Integer score;

        WebCheckConfig() {};

        void init(Map<String, Object> configMap) {
            try {
                altNameNum = (Map<String, Integer>)configMap.get("altnamenum");
                if(altNameNum.get("0") == -1) {
                    altNameNum.put("0", Integer.MIN_VALUE);
                }
                if(altNameNum.get("1") == -1) {
                    altNameNum.put("1", Integer.MAX_VALUE);
                }

                altNameDgaNum = (Map<String, Integer>)configMap.get("altnamedganum");
                if(altNameDgaNum.get("0") == -1) {
                    altNameDgaNum.put("0", Integer.MIN_VALUE);
                }
                if(altNameDgaNum.get("1") == -1) {
                    altNameDgaNum.put("1", Integer.MAX_VALUE);
                }

                altNameWhiteNum = (Map<String, Integer>)configMap.get("altnamewhitenum");
                if(altNameWhiteNum.get("0") == -1) {
                    altNameWhiteNum.put("0", Integer.MIN_VALUE);
                }
                if(altNameWhiteNum.get("1") == -1) {
                    altNameWhiteNum.put("1", Integer.MAX_VALUE);
                }

                reliability = (Map<String, Double>)configMap.get("reliability");
                if(reliability.get("0") == -1) {
                    reliability.put("0", Double.MIN_VALUE);
                }
                if(reliability.get("1") == -1) {
                    reliability.put("1", Double.MAX_VALUE);
                }

                score = (Integer) configMap.get("score");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Map<String, Integer> getAltNameNum() {
            return altNameNum;
        }

        public Map<String, Integer> getAltNameDgaNum() {
            return altNameDgaNum;
        }

        public Map<String, Integer> getAltNameWhiteNum() {
            return altNameWhiteNum;
        }

        public Map<String, Double> getReliability() {
            return reliability;
        }

        public Integer getScore() {
            return score;
        }
    }
}
