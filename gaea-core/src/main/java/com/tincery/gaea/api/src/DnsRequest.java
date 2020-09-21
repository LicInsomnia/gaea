package com.tincery.gaea.api.src;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DnsRequest {

    private String key;
    private String domain;
    private long capTime;


}
