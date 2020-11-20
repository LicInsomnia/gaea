package com.tincery.gaea.core.base.component.support;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*****
 * 证书链
 **/

@Getter
@Setter
public class CerChain {

    private String key;
    private List<String> cerChain;

}
