package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.datawarehouse.cer.config.property.CerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CerGmComplianceModuleUtils {

    public final static int bUnauthIssuer = 0;//未授权颁发者
    public final static int bWeakAlgo = 1;//弱算法
    public final static int bShortKeyLength = 2;//密钥长度过短
    public final static int bValidExpire = 3;//证书过期
    public final static int bOldVersion = 4;//版本过旧
    public final static int typeNum = 5;
    public final static ArrayList<String> descriptionList = new ArrayList<>(Arrays.asList("未授权颁发者", "弱算法", "公钥长度过短", "证书过期", "证书版本过旧"));
    private List<String> detailList = new ArrayList<>();
    private CerData cer;

    public CerGmComplianceModuleUtils(CerData cer) {
        this.cer = cer;
        for (int i = 0; i < typeNum; i++) {
            detailList.add("");
        }
    }

    public List<String> getDetailList() {
        return detailList;
    }

    public int checkIssuer() {
        String detail;
        int complianceType = 0;
        List<String> issuerWhiteList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getGmConfig().get("issuerWhite")).values());
        List<String> issuerBlackList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getGmConfig().get("issuerBlack")).values());
        String issuer = cer.getIssuer_cn() == null ? "" : cer.getIssuer_cn();
        if(issuerWhiteList != null && issuerWhiteList.size() > 0) {
            if(!issuerWhiteList.contains(issuer)) {
                complianceType = complianceType | (1 << bUnauthIssuer);
                detail = "未授权颁发者："  + issuer;
                detailList.set(bUnauthIssuer, detailList.get(bUnauthIssuer) + detail);
            }
        } else if(issuerBlackList != null && issuerBlackList.size() > 0){
            if(issuerBlackList.contains(issuer)) {
                complianceType = complianceType | (1 << bUnauthIssuer);
                detail = "未授权颁发者："  + issuer;
                detailList.set(bUnauthIssuer, detailList.get(bUnauthIssuer) + detail);
            }
        }
        return complianceType;
    }

    public int checkAlgo() {
        Map<String, Integer> gmPublicKeyOidLengthMap = (Map<String, Integer>)Config.cerProperties.getGmConfig().get("pubKeyOidMap");
        List<String> gmAlgoWhiteList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getGmConfig().get("algoWhite")).values());
        String detail;
        int complianceType = 0;
        int subjectPublicKeyLength = 0;
        if (cer.getSubjectpublickeylength() != null) {
            try {
                subjectPublicKeyLength = cer.getSubjectpublickeylength();
            } catch (Exception e) {
                complianceType = complianceType | (1 << typeNum);
            }
        } else {
            complianceType = complianceType | (1 << typeNum);
        }
        String subjectPublicKeyAlgoOid = "";
        if (cer.getSubjectpublickeyalgooid() != null && cer.getSubjectpublickeyalgooid().length() > 10) {
            subjectPublicKeyAlgoOid = cer.getSubjectpublickeyalgooid();
        } else {
            complianceType = complianceType | (1 << typeNum);
        }
        String subjectPublicKeyAlgo = "";
        if (cer.getSubjectpublickeyalgo() != null) {
            subjectPublicKeyAlgo = cer.getSubjectpublickeyalgo();
        }
        if (gmPublicKeyOidLengthMap != null && gmPublicKeyOidLengthMap.containsKey(subjectPublicKeyAlgoOid) && !subjectPublicKeyAlgoOid.equals("")) {
            if (subjectPublicKeyLength <= gmPublicKeyOidLengthMap.get(subjectPublicKeyAlgoOid)) {
                complianceType = complianceType | (1 << bShortKeyLength);
                detail = "公钥长度过短：算法为" + (subjectPublicKeyAlgo.equals("") ? subjectPublicKeyAlgoOid : subjectPublicKeyAlgo) + ",长度为" + subjectPublicKeyLength;
                detailList.set(bShortKeyLength, detailList.get(bShortKeyLength) + detail);
            }
        } else if (!subjectPublicKeyAlgoOid.equals("")){
            complianceType = complianceType | (1 << bWeakAlgo);
            detail = "公钥弱算法：算法为" + (subjectPublicKeyAlgo.equals("") ? subjectPublicKeyAlgoOid : subjectPublicKeyAlgo);
            detailList.set(bWeakAlgo, detailList.get(bWeakAlgo) + detail);
        }

        String signatureAlgooid = "";
        if (cer.getSignaturealgooid() != null && cer.getSignaturealgooid().length() > 10) {
            signatureAlgooid = cer.getSignaturealgooid();
        }else {
            complianceType = complianceType | (1 << typeNum);
        }
        String signatureAlgo = "";
        if (cer.getSignaturealgo() != null) {
            signatureAlgo = cer.getSignaturealgo();
        }
        if (gmAlgoWhiteList != null && !gmAlgoWhiteList.contains(signatureAlgooid) && !signatureAlgooid.equals("")) {
            complianceType = complianceType | (1 << bWeakAlgo);
            detail = "签名弱算法：算法为" + (signatureAlgo.equals("") ? signatureAlgooid : signatureAlgo);
            detailList.set(bWeakAlgo, detailList.get(bWeakAlgo) + detail);
        }
        return complianceType;
    }

    public int checkValid() {
        int complianceType = 0;
        String detail;
        try{
            long capTime = cer.getCaptime_n() == null ? 0L : cer.getCaptime_n();
            //long capTime = (long)cer.get("captime_n");
            if (cer.getValidafter() != null) {
                long valid = cer.getValidafter() == null ? 0L : cer.getValidafter();
                if (valid > 0 && valid < capTime) {
                    complianceType = complianceType | (1 << bValidExpire);
                    detail = "有效期过期：结束时间为" + ToolUtils.stamp2Date(valid);
                    detailList.set(bValidExpire, detailList.get(bValidExpire) + detail);
                }
            } else {
                complianceType = complianceType | (1 << typeNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complianceType;
    }

    public int checkVersion() {
        int complianceType = 0;
        int version;
        String detail;
        if (cer.getVersion() != null) {
            version = cer.getVersion();
            switch (version) {
                case 1:
                    complianceType = complianceType | (1 << bOldVersion);
                    detail = "证书版本过旧：为" + version;
                    detailList.set(bOldVersion, detailList.get(bOldVersion) + detail);
                    break;
                case 2:
                    complianceType = complianceType | (1 << bOldVersion);
                    detail = "证书版本过旧：为" + version;
                    detailList.set(bOldVersion, detailList.get(bOldVersion) + detail);
                    break;
                case 3:
                    break;
                default:
                    complianceType = complianceType | (1 << typeNum);
                    break;
            }
        }
        return complianceType;
    }
}
