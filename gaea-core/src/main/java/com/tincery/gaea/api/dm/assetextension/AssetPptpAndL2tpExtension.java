package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Data;

/**
 * @author Insomnia
 */
@Data
public class AssetPptpAndL2tpExtension extends BaseAssetExtension {

    /**
     * 协议版本
     */
    private String proName;
    /**
     * 认证协议
     */
    private String authenticationProtocol;
    /**
     * 认证算法
     */
    private String authenticationAlgorithm;

    /**
     * 加密算法
     */
    private String encryptionAlgorithm;

    @Override
    public boolean create(JSONObject jsonObject) {
        JSONObject pptpAndL2tpExtension = jsonObject.getJSONObject(HeadConst.FIELD.PPTP_L2TP_EXTENSION);
        if (null == pptpAndL2tpExtension) {
            return false;
        }
        this.proName = jsonObject.getString(HeadConst.FIELD.PRONAME);
        this.authenticationProtocol = pptpAndL2tpExtension.getString(HeadConst.FIELD.AUTHENTICATION_PROTOCOL);
        this.authenticationAlgorithm = pptpAndL2tpExtension.getString(HeadConst.FIELD.PPTP_AND_L2TP_AUTHENTICATION_ALGORITHM);
        this.encryptionAlgorithm = pptpAndL2tpExtension.getString(HeadConst.FIELD.ENCRYPTION_ALGORITHM);
        setKey();
        appendFlow(jsonObject);
        return false;
    }

    @Override
    public void setKey() {
        this.id = ToolUtils.getMD5(this.proName + "_" + this.authenticationProtocol + "_"
                + this.authenticationAlgorithm + "_" + this.encryptionAlgorithm);
    }

}
