package com.tincery.gaea.source.impsession.execute;

import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImpSessionLineSupport extends SrcLineSupport {

    @Autowired
    private IpChecker ipChecker;



}
