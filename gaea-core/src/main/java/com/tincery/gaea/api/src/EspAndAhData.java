package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.EspAndAhExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspAndAhData extends AbstractSrcData {

    private EspAndAhExtension espAndAhExtension;
    private Long endTime;
    private String key;

    public String getC2sSpi() {
        return this.espAndAhExtension.getC2sSpi();
    }

    public String getS2cSpi() {
        return this.espAndAhExtension.getS2cSpi();
    }

    public String getAbstractKey() {
        return this.clientIp + "_" + this.serverIp + "_" + this.clientPort + "_" + this.serverPort + "_" + this.protocol
                + "_" + this.espAndAhExtension.getC2sSpi() + "_" + this.espAndAhExtension.getS2cSpi();
    }

    public void setKey() {
        this.key = this.clientIp + "_" + this.serverIp + "_" + this.clientPort + "_" + this.serverPort + "_" + this.protocol;
    }

    public Long getPkt() {
        return this.upPkt + this.downPkt;
    }

    @Override
    public void adjust() {
        super.adjust();
        this.duration = this.endTime - this.capTime;
        if (getPkt() >= 3) {
            this.proName = 51 == this.protocol ? HeadConst.PRONAME.AH : HeadConst.PRONAME.ESP;
            this.dataType = 0;
        } else {
            this.proName = HeadConst.PRONAME.OTHER;
            this.dataType = -1;
        }
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.espAndAhExtension.toCsv(splitChar), JSONObject.toJSONString(this.espAndAhExtension)
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

    public void merge(EspAndAhData espAndAhData) {
        this.upPkt += espAndAhData.getUpPkt();
        this.downPkt += espAndAhData.getDownPkt();
        this.upByte += espAndAhData.getUpByte();
        this.downByte += espAndAhData.getDownByte();
        this.capTime = Math.min(this.capTime, espAndAhData.getCapTime());
        this.endTime = Math.max(this.capTime, espAndAhData.getCapTime());
        this.espAndAhExtension.merge(espAndAhData.getEspAndAhExtension());
    }

}
