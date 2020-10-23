package com.tincery.gaea.source.openven.execute;

import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.gaea.core.base.component.support.CipherSuiteDetector;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Insomnia
 */
@Component
public class OpenVpnLineSupport extends SrcLineSupport {

    @Autowired
    private CipherSuiteDetector cipherSuiteDetector;

    public CipherSuiteDO getCipherSuite(String cipherSuite) {
        return this.cipherSuiteDetector.getCipherSuite(cipherSuite);
    }

}
