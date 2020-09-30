package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.api.src.ImpSessionData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author gongxuanzhang
 */
@Component
public class ImpSessionReceiver extends AbstractSrcReceiver<ImpSessionData> {


    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.SESSION_HEADER;
    }


    @Autowired
    public void setAnalysis(ImpSessionLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Override
    public void init() {

    }
}
