package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.base.CertDo;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.datawarehouse.cer.config.property.CerProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiuMing
 * <p>{@code CerReliabilityUtils} 是证书不可靠检测实现类.</>
 */
public class CerReliabilityModuleUtils {
    @Autowired
    private CerProperties cerProperties;

    public final static int bNoAuth = 0;//证书链验证失败
    public final static int bWeakAuth = 1;//通过基于common_name的弱证书链验证
    public final static int bSelfSigned = 2;//自签名
    public final static int bLessInfo = 3;//证书信息缺失
    public final static int bNotWebUrl = 4;//subject_cn不是url
    public final static int bInFAAS = 5;//subject_cn属于常见可疑集合
    public final static int bLongValid = 6;//有效期过长
    public final static int bErrorValid = 7;//有效期错误
    public final static int bSelfSignedInFAS = 8;//subject_cn属于常见可信集合，但为自签名
    //public final static ArrayList<String> detailList = new ArrayList<>(Arrays.asList("证书链验证失败", "通过基于common_name的弱证书链验证", "自签名", "证书信息缺失", "subject_cn不是url", "subject_cn属于常见可疑集合",
    //        "有效期过长", "有效期错误", "subject_cn属于常见可信集合，但为自签名"));
    public final static int typeNum = 9;
    private CerData cer;
    private Boolean isSelfSigned = false;
    private Boolean inFAS = false;
    private ArrayList<String> detailList = new ArrayList<>();

    public CerReliabilityModuleUtils(CerData cer) {
        this.cer = cer;
        for (int i = 0; i < typeNum; i++) {
            detailList.add("");
        }
    }

    public ArrayList<String> getDetailList() {
        return detailList;
    }

    //计算自签名得分
    public Map<String, Object> calcSelfSignedScore() {
        List<String> rootCaList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("rootCAList")).values());
        int reliabilityType = 0;
        double score = 0D;
        String detail;
        isSelfSigned = false;
        if (cer.getSubjectCommonName() != null && cer.getIssuerCommonName() != null) {
            String subjectCN = cer.getSubjectCommonName();
            String issuerCN = cer.getIssuerCommonName();
            if (subjectCN.equals(issuerCN)) {
                String sha1 = cer.getId();
                if (rootCaList != null && !rootCaList.contains(sha1)) {
                    score -= 0.25;
                    isSelfSigned = true;
                    reliabilityType = reliabilityType | (1 << bSelfSigned);
                    detail = "自签名：subject_cn为" + subjectCN;
                    detailList.set(bSelfSigned, detailList.get(bSelfSigned) + detail);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算证书信息得分
    public Map<String, Object> calcSubContentNumScore() {
        int reliabilityType = 0;
        double score = 0D;
        String detail;
        if (cer.getSubjectCommonName() != null) {
            score += 0.1;
        } else {
            detail = "证书信息缺失：subject_cn信息缺失";
            detailList.set(bLessInfo, detailList.get(bLessInfo) + detail);
        }
        if (cer.getSubjectOrganizationName() != null) {
            score += 0.1;
        } else {
            detail = "证书信息缺失：subject_o信息缺失";
            detailList.set(bLessInfo, detailList.get(bLessInfo) + detail);
        }
        if (cer.getSubjectCountryName() != null) {
            score += 0.1;
        } else {
            detail = "证书信息缺失：subject_c信息缺失";
            detailList.set(bLessInfo, detailList.get(bLessInfo) + detail);
        }
        if (cer.getSubjectLocalityName() != null) {
            score += 0.1;
        } else {
            detail = "证书信息缺失：subject_l信息缺失";
            detailList.set(bLessInfo, detailList.get(bLessInfo) + detail);
        }
        if (score < 0.4) {
            reliabilityType = reliabilityType | (1 << bLessInfo);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算subject_cn是否为url得分
    public Map<String, Object> calcSubCnIsUrlScore() {
        String detail;
        int reliabilityType = 0;
        double score = 0D;
        if (cer.getSubjectCommonName() != null) {
            String url = cer.getSubjectCommonName();
            url = url.replace("*", "a");
            url = url.replace("?", "a");
            if (IsUrl(url)) {
                score += 0.25;
            } else {
                reliabilityType = reliabilityType | (1 << bNotWebUrl);
                detail = "subject_cn为" + url + ",不符合域名规范";
                detailList.set(bNotWebUrl, detailList.get(bNotWebUrl) + detail);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算subject_cn是否属于常见可信集合得分
    public Map<String, Object> calcSubCnInFASScore() {
        List<String> fasCaList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("FASList")).values());
        double score = 0D;
        inFAS = false;
        String subCN = cer.getSubjectCommonName();
        if (cer.getSubjectCommonName() != null) {
            if (fasCaList.contains(subCN)) {
                inFAS = true;
                score += 0.25;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", 0);
        return result;
    }

    //计算subject_cn是否属于常见可疑集合得分
    public Map<String, Object> calcSubCnInFAASScore() {
        List<String> faasCaList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("FAASList")).values());
        String detail;
        int reliabilityType = 0;
        double score = 0D;
        String subCN;
        if (cer.getSubjectCommonName() != null) {
            subCN = cer.getSubjectCommonName();
            if (faasCaList != null && faasCaList.contains(subCN)) {
                reliabilityType = reliabilityType | (1 << bInFAAS);
                score -= 0.25;
                detail = "subject_cn属于常见可疑集合,为" + subCN;
                detailList.set(bInFAAS, detailList.get(bInFAAS) + detail);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算有效期过长得分
    public Map<String, Object> calcLongValidLenScore() {
        String detail;
        int reliabilityType = 0;
        double score = 0D;
        String temp;
        long validBefore = (long) 0, validLength = (long) 0;
        if (cer.getValidBefore() != null) {
            temp = cer.getValidBefore().toString();
            if (temp.length() > 0 && temp.length() < 14) {
                validBefore = cer.getValidBefore();
            } else {
                score -= -0.25;
                reliabilityType = reliabilityType | (1 << bErrorValid);
                detail = "有效期错误，开始时间为" + temp;
                detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
                Map<String, Object> result = new HashMap<>();
                result.put("score", score);
                result.put("reliabilityType", reliabilityType);
                return result;
            }
        }
        if (cer.getValidLength() != null) {
            temp = cer.getValidLength().toString();
            if (temp.length() > 0 && temp.length() < 14) {
                validLength = cer.getValidLength();
            } else {
                score -= -0.25;
                reliabilityType = reliabilityType | (1 << bErrorValid);
                detail = "有效期错误，长度为" + temp;
                detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
                Map<String, Object> result = new HashMap<>();
                result.put("score", score);
                result.put("reliabilityType", reliabilityType);
                return result;
            }
        }
        if ((validBefore >= 1519833600000L) && (validLength > (86400000L * 825L))) {//2 years
            score -= -0.25;
        } else if ((validBefore < 1519833600000L) && (validLength > (86400000L * 1191L))) {//3 years
            score -= -0.25;
        }
        if (score < 0) {
            reliabilityType = reliabilityType | (1 << bLongValid);
            detail = "有效期过长，为" + validLength;
            detailList.set(bLongValid, detailList.get(bLongValid) + detail);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算不正常行为得分
    public Map<String, Object> calcAbnormalActScore() {
        String detail;
        int reliabilityType = 0;
        double score = 0D;
        if (isSelfSigned && inFAS) {
            score -= 0.75;
            reliabilityType = reliabilityType | (1 << bSelfSignedInFAS);
            String subCN = cer.getSubjectCommonName();
            detail = "subject_cn属于常见可信集合，但为自签名，为" + subCN;
            detailList.set(bSelfSignedInFAS, detailList.get(bSelfSignedInFAS) + detail);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //计算证书链验证得分
    public Map<String, Object> calcReliableAuthScore(Map<String, Object> sha1WithChain, Map<String, Object> sha1WithRaw, PKIXParameters params) throws CertificateException {
        List<String> caList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("CAList")).values());
        int reliabilityType = 0;
        double score = 0D;
        String[] elements;
        String certStr;
        List<X509Certificate> mylist = new ArrayList<>();
        List<List<X509Certificate>> totalList = new ArrayList<>();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //System.out.println(cer.get("sha1").toString());
        boolean authFlagLoose = false;
        if (cer.getIssuerCommonName() != null && caList.contains(cer.getIssuerCommonName())) {
            authFlagLoose = true;
        }
        /*
        List<String> chainList = new ArrayList<>();
        if(sha1WithChain.containsKey(cer.get("sha1").toString())) {
            chainList = (List) sha1WithChain.get(cer.get("sha1").toString());
            for(String chain : chainList) {
                //chain = sha1WithChain.get(cer.get("sha1").toString()).toString();
                elements = chain.split(";");
                for (String element : elements) {
                    if(sha1WithRaw.containsKey(element)) {
                        certStr = sha1WithRaw.get(element).toString();
                        BASE64Decoder decoder = new BASE64Decoder();
                        byte[] byteCert = decoder.decodeBuffer(certStr);
                        //转换成二进制流
                        try {
                            X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(byteCert));
                            mylist.add(cert);
                        }
                        catch(CertificateException e) {
                            //System.out.println(e.getMessage());
                        }
                    }
                }
                totalList.add(new ArrayList<>());
                totalList.get(totalList.size()-1).addAll(mylist);
                mylist.clear();
            }
        }
        else {
            certStr = cer.get("raw").toString();
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] byteCert = decoder.decodeBuffer(certStr);
            chainList.add((cer.get("sha1").toString()+";"));
            //转换成二进制流
            try {
                X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(byteCert));
                mylist.add(cert);
                totalList.add(mylist);
            }
            catch(CertificateException e) {
                //System.out.println(e.getMessage());
            }
        }
        boolean authFlag = false;
        for(List<X509Certificate> subList : totalList) {
            if(subList.size() != 0) {
                CertPath cp = cf.generateCertPath(subList);
                CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
                try {
                    cpv.validate(cp, params);
                    authFlag = true;
                    for(X509Certificate certObj : subList) {
                        String subject = certObj.getSubjectX500Principal().getName("RFC1779");
                        String subjectCN ;
                        if(subject.contains("CN")) {
                            int begin = subject.indexOf("CN");
                            int end = subject.indexOf(',', subject.indexOf("CN"));
                            if(begin != -1 && end != -1) {
                                subjectCN = subject.substring(begin+3, end);
                            } else if(begin != -1) {
                                subjectCN = subject.substring(begin+3);
                            }
                            /*
                            if(!GetConfig.getCAList(configPath).contains(subjectCN))
                            {
                                if(subjectCN.indexOf("CA")!=-1 || subjectCN.indexOf("Certification Authority")!=-1 || subjectCN.indexOf("Certificate Authority")!=-1)
                                {
                                    addInterCAList.add(subjectCN);
                                    chain = chainList.get(k).toString();
                                    elements = chain.split(";");
                                    addInterCASha1List.add(elements[i]);
                                }
                                else if(i > 0)
                                {
                                    addUnknowCAList.add(subjectCN);
                                    chain = chainList.get(k).toString();
                                    elements = chain.split(";");
                                    addUnknowCASha1List.add(elements[i]);
                                }
                            }
                        }
                    }
                } catch (CertPathValidatorException cpve) {
                    //System.out.println("Validation failure, cert[" + cpve.getIndex() + "] :" + cpve.getMessage());
                }
            }
        }
        */
        Boolean authFlag = Boolean.FALSE;
        String detail;
        if (authFlag) {
            score += 0.5;
            //System.out.println("auth");
        } else if (authFlagLoose) {
            score += 0.25;
            //System.out.println("weak auth");
            //reliabilityType = reliabilityType | (1<<bWeakAuth);//暂时注释掉
        } else {
            reliabilityType = reliabilityType | (1 << bNoAuth);
            detail = "基于common_name的证书链验证未通过，证书sha1为" + cer.getId();
            detailList.set(bNoAuth, detailList.get(bNoAuth) + detail);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reliabilityType", reliabilityType);
        return result;
    }

    //判断否为url
    private boolean IsUrl(String url) {
        if (url.length() > 255) {
            return false;
        }
        String[] arr = url.split("[.]");
        for (String str : arr) {
            if (str.length() > 63 || str.length() == 0) {
                return false;
            }
            for (int j = 0; j < str.length(); j++) {
                if (!((str.charAt(j) <= 'Z' && str.charAt(j) >= 'A')
                        || (str.charAt(j) <= 'z' && str.charAt(j) >= 'a')
                        || (str.charAt(j) <= '9' && str.charAt(j) >= '0')
                        || (str.charAt(j) == '-'))) {
                    return false;
                }
            }
        }
        if (!((url.charAt(0) <= 'Z' && url.charAt(0) >= 'A')
                || (url.charAt(0) <= 'z' && url.charAt(0) >= 'a')
                || (url.charAt(0) <= '9' && url.charAt(0) >= '0'))) {
            return false;
        }
        return (url.charAt(url.length() - 1) <= 'Z' && url.charAt(url.length() - 1) >= 'A')
                || (url.charAt(url.length() - 1) <= 'z' && url.charAt(url.length() - 1) >= 'a')
                || (url.charAt(url.length() - 1) <= '9' && url.charAt(url.length() - 1) >= '0');
    }
}
