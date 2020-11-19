package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.datawarehouse.cer.config.property.CerProperties;
import java.util.*;

/**
 * @author LiuMing
 * <p>{@code CerComplianceUtils} 是证书不合规检测实现类.</>
 */
public class CerComplianceModuleUtils {

    public final static int bOldVersion = 0;//证书版本过旧
    public final static int bErrorVersion = 1;//证书版本错误
    public final static int bNoSerialNum = 2;//无序列号
    public final static int bLongSerialNum = 3;//序列号过长
    public final static int bNegSerialNum = 4;//序列号为负
    public final static int bLateValidBefore = 5;//有效期开始过晚
    public final static int bEarlyValidAfter = 6;//有效期结束过早
    public final static int bValidDisorder = 7;//有效期开始与结束乱序
    public final static int bLongValid = 8;//有效期过长
    public final static int bErrorValid = 9;//有效期错误
    public final static int bShortPubKey = 10;//公钥长度过短
    public final static int bWeakPubAlgo = 11;//公钥弱算法
    public final static int bWeakSignAlgo = 12;//签名弱算法
    public final static int bSpecialSubjectCN = 13;//
    public final static int typeNum = 14;//
    public static int[] algorithmIncompliance = {bWeakPubAlgo, bWeakSignAlgo};
    public static int[] pubKeyShort = {bShortPubKey};
    public final static ArrayList<String> descriptionList = new ArrayList<>(Arrays.asList("证书版本过旧", "证书版本错误", "无序列号", "序列号过长", "序列号为负", "有效期开始过晚", "有效期结束过早", "有效期开始与结束乱序",
            "有效期过长", "有效期错误", "公钥长度过短", "公钥弱算法", "签名弱算法", "特殊subject_cn"));
    private CerData cer;
    private ArrayList<String> detailList = new ArrayList<>();


    public CerComplianceModuleUtils(CerData cer) {
        this.cer = cer;
        for (int i = 0; i < typeNum; i++) {
            detailList.add("");
        }
    }

    public ArrayList<String> getDetailList() {
        return detailList;
    }

    /**
     * @author LiuMing
     * 证书版本检查
     */
    public Integer checkVersion() {
        int complianceType = 0;
        String version;
        String detail;
        if (cer.getVersion() != null) {
            version = cer.getVersion().toString();
            switch (cer.getVersion()) {
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
                    complianceType = complianceType | (1 << bErrorVersion);
                    detail = "证书版本错误：为" + version;
                    detailList.set(bErrorVersion, detailList.get(bErrorVersion) + detail);
                    break;
            }
        } else {
            complianceType = complianceType | (1 << bErrorVersion);
            detail = "证书版本错误：无版本号";
            detailList.set(bErrorVersion, detailList.get(bErrorVersion) + detail);
        }
        return complianceType;
    }

    /**
     * @author LiuMing
     * 证书序列号检查
     */
    public Integer checkSerialNum() {
        int complianceType = 0;
        String serialNum;
        String detail;
        int maxLength = 40;
        if (cer.getSerialnum() != null) {
            serialNum = cer.getSerialnum();
            if (serialNum.length() > 2 && serialNum.substring(0, 2).equals("0x")) {
                serialNum = serialNum.substring(2);
            }
            if (serialNum.length() > maxLength) {
                complianceType = complianceType | (1 << bLongSerialNum);
                detail = "序列号过长：长度为" + serialNum.length();
                detailList.set(bLongSerialNum, detailList.get(bLongSerialNum) + detail);
            }
            if (serialNum.length() > 0 && serialNum.charAt(0) == '-') {
                complianceType = complianceType | (1 << bNegSerialNum);
                detail = "序列号为负：为" + serialNum;
                detailList.set(bNegSerialNum, detailList.get(bNegSerialNum) + detail);
            }
        } else {
            complianceType = complianceType | (1 << bNoSerialNum);
            detail = "无序列号";
            detailList.set(bNoSerialNum, detailList.get(bNoSerialNum) + detail);
        }
        return complianceType;
    }

    /**
     * @author LiuMing
     * 证书有效期检查
     */
    public Integer checkValidTime() {
        int complianceType = 0;
        long capTime;
        long validBefore = 0;
        long validAfter = 0;
        long validLength = 0;
        String temp;
        String detail;
        capTime = cer.getCaptime_n();
        if (capTime / 1000000000000000L >= 1) {
            capTime = cer.getCaptime_n() / 1000;
        } else {
            capTime = cer.getCaptime_n();
        }
        if (cer.getValidbefore() != null) {
            temp = cer.getValidbefore().toString();
            if (temp.length() > 0 && temp.length() < 14) {
                validBefore = Long.parseLong(temp);
            } else {
                complianceType = complianceType | (1 << bErrorValid);
                detail = "有效期错误：开始时间为" + temp;
                detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
            }
        } else {
            complianceType = complianceType | (1 << bErrorValid);
            detail = "有效期错误：无开始时间";
            detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
        }
        if (cer.getValidafter() != null) {
            temp = cer.getValidafter().toString();
            if (temp.length() > 0 && temp.length() < 14) {
                validAfter = Long.parseLong(temp);
            } else {
                complianceType = complianceType | (1 << bErrorValid);
                detail = "有效期错误：结束时间为" + temp;
                detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
            }
        } else {
            complianceType = complianceType | (1 << bErrorValid);
            detail = "有效期错误：无结束时间";
            detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
        }
        if (cer.getValidlength() != null) {
            temp = cer.getValidlength().toString();
            if (temp.length() > 0 && temp.length() < 14) {
                validLength = Long.parseLong(temp);
            } else {
                complianceType = complianceType | (1 << bErrorValid);
                detail = "有效期错误：有效期长度为" + temp;
                detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
            }
        } else {
            complianceType = complianceType | (1 << bErrorValid);
            detail = "有效期错误：无有效期长度";
            detailList.set(bErrorValid, detailList.get(bErrorValid) + detail);
        }
        if ((complianceType & (1 << bErrorValid)) == 0) {
            if ((validBefore > capTime)) {
                complianceType = complianceType | (1 << bLateValidBefore);
                detail = "有效期开始过晚：开始时间为" + ToolUtils.stamp2Date(validBefore);
                detailList.set(bLateValidBefore, detailList.get(bLateValidBefore) + detail);
            }
            if ((validAfter < capTime)) {
                complianceType = complianceType | (1 << bEarlyValidAfter);
                detail = "有效期结束过早：结束时间为" + ToolUtils.stamp2Date(validAfter);
                detailList.set(bEarlyValidAfter, detailList.get(bEarlyValidAfter) + detail);
            }
            if (validBefore > validAfter) {
                complianceType = complianceType | (1 << bValidDisorder);
                detail = "有效期开始与结束乱序：开始时间为" + ToolUtils.stamp2Date(validBefore) + ",结束时间为" + ToolUtils.stamp2Date(validAfter);
                detailList.set(bValidDisorder, detailList.get(bValidDisorder) + detail);
            }
            if ((validBefore >= 1519833600000L) && (validLength > (86400000L * 825L))) { //两年最长有效期
                complianceType = complianceType | (1 << bLongValid);
                detail = "有效期过长：长度为" + validLength;
                detailList.set(bLongValid, detailList.get(bLongValid) + detail);
            }
            if ((validBefore < 1519833600000L) && (validLength > (86400000L * 1191L))) { //三年最长有效期
                complianceType = complianceType | (1 << bLongValid);
                detail = "有效期过长：长度为" + validLength;
                detailList.set(bLongValid, detailList.get(bLongValid) + detail);
            }
        }
        return complianceType;
    }

    /*
     * @author LiuMing
     * @see 证书公钥检查
     */
    public Integer checkPublicKey() {
        Map<String, Integer> publicKeyAlgoMap = (Map<String, Integer>)Config.cerProperties.getDefaultConfig().get("pubKeyOidMap");
        String detail;
        int complianceType = 0;
        int subjectPublicKeyLength = 0;
        if (cer.getSubjectpublickeylength() != null) {
            subjectPublicKeyLength = cer.getSubjectpublickeylength();
        }
        String subjectPublicKeyAlgoOid = "";
        if (cer.getSubjectpublickeyalgooid() != null) {
            subjectPublicKeyAlgoOid = cer.getSubjectpublickeyalgooid() ;
        }
        if (publicKeyAlgoMap != null && publicKeyAlgoMap.containsKey(subjectPublicKeyAlgoOid)) {
            if (subjectPublicKeyLength <= publicKeyAlgoMap.get(subjectPublicKeyAlgoOid)) {
                complianceType = complianceType | (1 << bShortPubKey);
                detail = "公钥长度过短：算法为" + subjectPublicKeyAlgoOid + ",长度为" + subjectPublicKeyLength;
                detailList.set(bShortPubKey, detailList.get(bShortPubKey) + detail);
            }
        } else {
            complianceType = complianceType | (1 << bWeakPubAlgo);
            detail = "公钥弱算法：算法为" + subjectPublicKeyAlgoOid;
            detailList.set(bWeakPubAlgo, detailList.get(bWeakPubAlgo) + detail);
        }
        return complianceType;
    }

    /*
     * @author LiuMing
     * @see 证书签名检查
     */
    public Integer checkSignKey() {
        List<String> signAlgoOidList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("signAlgoOidList")).values());
        int complianceType = 0;
        String detail;
        String signatureAlgooid = "";
        if (cer.getSignaturealgooid() != null) {
            signatureAlgooid = cer.getSignaturealgooid();
        }
        if (signAlgoOidList != null && !signAlgoOidList.contains(signatureAlgooid)) {
            complianceType = complianceType | (1 << bWeakSignAlgo);
            detail = "签名弱算法：算法为" + signatureAlgooid;
            detailList.set(bWeakSignAlgo, detailList.get(bWeakSignAlgo) + detail);
        }
        return complianceType;
    }

    /**
     * @author LiuMing
     * 证书自签名判断
     */
    public Boolean checkSelfSigned() {
        Boolean isSelfSigned = Boolean.FALSE;
        String issuerCN = "";
        String subjectCN = "";
        if (cer.getIssuer_cn() != null) {
            issuerCN = cer.getIssuer_cn();
        }
        if (cer.getSubject_cn() != null) {
            subjectCN = cer.getSubject_cn();
        }
        if (issuerCN.equals(subjectCN)) {
            isSelfSigned = Boolean.TRUE;
        }
        return isSelfSigned;
    }

    public Integer checkSubjectCN() {
        List<String> subjectCNList = new ArrayList<>(((Map<Integer, String>)Config.cerProperties.getDefaultConfig().get("subjectCN")).values());
        int complianceType = 0;
        String subjectCN = "";
        String detail;
        if (cer.getSubject_cn() != null) {
            subjectCN = cer.getSubject_cn();
        }

        if (subjectCNList != null && subjectCNList.contains(subjectCN)) {
            complianceType = complianceType | (1 << bSpecialSubjectCN);
            detail = "特殊subject_cn：为" + subjectCN;
            detailList.set(bSpecialSubjectCN, detailList.get(bSpecialSubjectCN) + detail);
        }
        return complianceType;
    }
}
