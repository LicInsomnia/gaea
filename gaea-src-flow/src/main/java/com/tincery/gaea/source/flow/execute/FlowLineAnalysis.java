package com.tincery.gaea.source.flow.execute;


import com.tincery.gaea.api.base.FlowProtocolDetails;
import com.tincery.gaea.api.base.FlowStatistic;
import com.tincery.gaea.api.src.FlowData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import org.springframework.stereotype.Component;


/**
 * @author gxz
 */
@Component
public class FlowLineAnalysis implements SrcLineAnalysis<FlowData> {

    /**
     * 将一条记录包装成实体类
     * 0.source 1.type  2.proname 3.pktnum    4.bytenum   5.captime_n(us)
     */
    @Override
    public FlowData pack(String line) {
        FlowData flowData = new FlowData();
        String[] elements = StringUtils.FileLineSplit(line);
        long pktNum = Long.parseLong(elements[3]);
        if (pktNum == 0) {
            return null;
        }
        long byteNum = Long.parseLong(elements[4]);
        String source = elements[0];
        int type = Integer.parseInt(elements[1]);
        String proName = elements[2];
        Long capTime = DateUtils.validateTime(Long.parseLong(elements[5])) / DateUtils.HOUR * DateUtils.HOUR;
        FlowProtocolDetails flowProtocolDetails = new FlowProtocolDetails(type, proName, pktNum, byteNum);
        FlowStatistic flowStatistic = null;
        switch (type) {
            case 3:
                flowStatistic = new FlowStatistic(source, pktNum, byteNum, capTime, false, false, flowProtocolDetails);
                break;
            case 5:
                flowStatistic = new FlowStatistic(source, capTime, true, false, flowProtocolDetails);
                break;
            default:
                flowStatistic = new FlowStatistic(source, capTime, false, false, flowProtocolDetails);
                break;
        }
        flowData.setFlowStatistic(flowStatistic);
        return flowData;
    }

}
