package com.tincery.gaea.api.dm;

import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class SessionMergeData extends AbstractDataWarehouseData {

    /**
     * 应用信息
     */
    protected ApplicationInformationBO application;
    protected Map<String, ApplicationInformationBO> applicationElements;
    protected Set<String> appCheckModes;
    protected String checkMode;

    public String targetSessionKey() {

        new JSONObject(new HashMap());
        return ToolUtils.getMD5(this.targetName + "_" + this.userId + "_" + this.serverIp);
    }

}
