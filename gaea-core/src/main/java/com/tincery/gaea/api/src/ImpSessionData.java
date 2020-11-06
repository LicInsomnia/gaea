package com.tincery.gaea.api.src;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.src.extension.SessionExtension;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImpSessionData extends AbstractSrcData {

    SessionExtension sessionExtension = new SessionExtension();

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.sessionExtension.toCsv(splitChar),
                JSONObject.toJSONString(this.sessionExtension)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


    @Override
    public String getDateSetFileName(String category) {
        category = category.replace("imp", "");
        return FileUtils.getCsvDataFile(category, this.capTime, "imp." + NodeInfo.getNodeName());
    }

    void setProName(ApplicationProtocol applicationProtocol, PayloadDetector payloadDetector) {
        String key = this.protocol + "_" + this.serverPort;
        String proName = payloadDetector.getProName(this.protocol, this.serverPort, this.clientPort,
                this.sessionExtension.getUpPayLoad(), this.sessionExtension.getDownPayLoad());
        if ("other".equals(proName)) {
            ApplicationInformationBO applicationInformation = applicationProtocol.getApplication(key);
            if (null != applicationInformation) {
                this.proName = applicationInformation.getProName();
            }
        }
    }

    public void setPayload(String payload) {
        if (this.getDataType() == 1) {
            this.sessionExtension.setDownPayLoad(payload);
        } else {
            this.sessionExtension.setUpPayLoad(payload);
        }
    }

    public String getPairKey() {
        String tag = this.dataType == 1 ? "2" : "1";
        return tag + "_" + this.clientIp + this.serverIp + this.clientPort + this.serverPort + this.protocol + this.capTime + this.targetName;
    }

    public String getKey() {
        return this.dataType + "_" + this.clientIp + this.serverIp + this.clientPort + this.serverPort + this.protocol + this.capTime + this.targetName;
    }

    public void merge(ImpSessionData impsessionData) {
        long minCaptime = Math.min(this.capTime, impsessionData.capTime);
        long endTime = Math.max(this.capTime + this.duration, impsessionData.capTime + impsessionData.duration);
        this.capTime = minCaptime;
        this.duration = endTime - capTime;
        this.syn = this.syn || impsessionData.syn;
        this.fin = this.fin || impsessionData.fin;
        this.upPkt += impsessionData.upPkt;
        this.downPkt += impsessionData.downPkt;
        this.upByte += impsessionData.upByte;
        this.downByte += impsessionData.downByte;
        if (impsessionData.dataType == 1 && !impsessionData.sessionExtension.getDownPayLoad().isEmpty()) {
            this.sessionExtension.setDownPayLoad(impsessionData.sessionExtension.getDownPayLoad());
        }
        if (impsessionData.dataType == 2 && !impsessionData.sessionExtension.getUpPayLoad().isEmpty()) {
            this.sessionExtension.setUpPayLoad(impsessionData.sessionExtension.getUpPayLoad());
        }
        this.dataType = 0;
    }


}
