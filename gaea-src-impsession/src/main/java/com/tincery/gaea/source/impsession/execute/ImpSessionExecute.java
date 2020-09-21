package com.tincery.gaea.source.impsession.execute;

import com.tincery.common.base.AbstractCollectExecute;
import com.tincery.common.mgt.HeadConst;
import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.source.impsession.config.property.ImpSessionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gongxuanzhang
 */
@Component
public class ImpSessionExecute extends AbstractCollectExecute<ImpSessionProperties, ImpSessionData> {


    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }


    @Autowired
    public void setAnalysis(ImpSessionLineAnalysis analysis) {
        this.analysis = analysis;
    }


    @Override
    @Autowired
    public void setProperties(ImpSessionProperties properties) {
        this.properties = properties;
    }


}
