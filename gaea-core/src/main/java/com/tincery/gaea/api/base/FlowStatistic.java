package com.tincery.gaea.api.base;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class FlowStatistic {

    String source;
    Long pktNum;
    Long byteNum;
    Long capTime;
    Boolean imp;
    Boolean asset;

    @JSONField(serialize = false)
    Map<String, FlowProtocolDetails> flowProtocolDetailsMap;

    List<FlowProtocolDetails> flowProtocolDetailsList;

    public FlowStatistic(String source, Long pktNum, Long byteNum, Long capTime, Boolean imp, Boolean asset, FlowProtocolDetails flowProtocolDetails) {
        this.source = source;
        this.pktNum = pktNum;
        this.byteNum = byteNum;
        this.capTime = capTime;
        this.imp = imp;
        this.asset = asset;
        this.flowProtocolDetailsMap = new ConcurrentHashMap<>();
        this.flowProtocolDetailsMap.put(flowProtocolDetails.getKey(), flowProtocolDetails);
    }

    public FlowStatistic(String source, Long capTime, Boolean imp, Boolean asset, FlowProtocolDetails flowProtocolDetails) {
        this.source = source;
        this.pktNum = 0L;
        this.byteNum = 0L;
        this.capTime = capTime;
        this.imp = imp;
        this.asset = asset;
        this.flowProtocolDetailsMap = new ConcurrentHashMap<>();
        this.flowProtocolDetailsMap.put(flowProtocolDetails.getKey(), flowProtocolDetails);
    }

    public void appendPktNum(Long pktNum) {
        this.pktNum += pktNum;
    }

    public void appendByteNum(Long byteNum) {
        this.byteNum += byteNum;
    }

    public String getKey() {
        return source + "_" + capTime;
    }

    public void merge(FlowStatistic flowStatistic) {
        this.appendPktNum(flowStatistic.getPktNum());
        this.appendByteNum(flowStatistic.getByteNum());
        Map<String, FlowProtocolDetails> flowProtocolDetailsMap = flowStatistic.getFlowProtocolDetailsMap();
        for (Map.Entry<String, FlowProtocolDetails> entry : flowProtocolDetailsMap.entrySet()) {
            if (this.flowProtocolDetailsMap.containsKey(entry.getKey())) {
                FlowProtocolDetails buffer = this.flowProtocolDetailsMap.get(entry.getKey());
                buffer.merge(entry.getValue());
            } else {
                this.flowProtocolDetailsMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
