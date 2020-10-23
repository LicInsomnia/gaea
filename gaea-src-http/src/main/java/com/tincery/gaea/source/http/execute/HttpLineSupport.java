package com.tincery.gaea.source.http.execute;

import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpLineSupport extends SrcLineSupport {

    @Autowired
    private IpSelector ipSelector;

    public Location getLocation(String ip) {
        return ipSelector.getCommonInformation(ip);
    }


}
