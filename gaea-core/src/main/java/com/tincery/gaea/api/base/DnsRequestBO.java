package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DnsRequestBO implements Comparable<DnsRequestBO> {

    private String key;
    private String domain;
    private Long capTime;

    public DnsRequestBO(String key, String domain, long capTime) {
        this.key = key;
        this.domain = domain;
        this.capTime = capTime;
    }

    @Override
    public int compareTo(DnsRequestBO o) {
        return Long.compare(this.capTime, o.capTime);
    }
}
