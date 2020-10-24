package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Administrator
 */
@Getter
@Setter
public class FlowProtocolDetails {

    private Integer type;
    private String proName;
    private Long pktNum;
    private Long byteNum;

    public FlowProtocolDetails(Integer type, String proName, Long pktNum, Long byteNum) {
        this.type = type;
        this.proName = proName;
        this.pktNum = pktNum;
        this.byteNum = byteNum;
    }

    public String getKey() {
        return type + "_" + proName;
    }

    public void merge(FlowProtocolDetails flowProtocolDetails) {
        this.pktNum += flowProtocolDetails.pktNum;
        this.byteNum += flowProtocolDetails.byteNum;
    }

}
