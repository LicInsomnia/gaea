package com.tincery.gaea.api.src;


import com.tincery.gaea.api.base.FlowProtocolDetails;
import com.tincery.gaea.api.base.FlowStatistic;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class FlowData extends AbstractSrcData {

    private FlowStatistic flowStatistic;

    public String getFlowKey() {
        return this.flowStatistic.getKey();
    }

    public void merge(FlowData flowData) {
        this.flowStatistic.merge(flowData.flowStatistic);
    }

    @Override
    public void adjust() {
        List<FlowProtocolDetails> flowProtocolDetailsList = new ArrayList<>(this.flowStatistic.getFlowProtocolDetailsMap().values());
        this.flowStatistic.setFlowProtocolDetailsList(flowProtocolDetailsList);
        this.flowStatistic.adjust();
    }

}
