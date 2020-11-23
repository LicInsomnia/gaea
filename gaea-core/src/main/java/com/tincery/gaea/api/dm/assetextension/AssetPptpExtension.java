package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AssetPptpExtension extends BaseAssetExtension {

    /**
     * 协议版本
     */
    private String proName;
    /**
     * 认证协议
     */
    private String authProtocol;
    /**
     * 认证算法
     */
    private String authenticationAlgorithm;

    @Override
    public boolean create(JSONObject jsonObject) {
        JSONObject pptpAndL2tpExtension = jsonObject.getJSONObject(HeadConst.FIELD.PPTP_L2TP_EXTENSION);
        if (null == pptpAndL2tpExtension) {
            return false;
        }
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.authProtocol = pptpAndL2tpExtension.getString(HeadConst.FIELD.AUTHENTICATION_PROTOCOL);
        this.authenticationAlgorithm = pptpAndL2tpExtension.getString(HeadConst.FIELD.PPTP_AND_L2TP_AUTHENTICATION_ALGORITHM);
        return false;
    }

    @Override
    public void setKey() {

    }

}
