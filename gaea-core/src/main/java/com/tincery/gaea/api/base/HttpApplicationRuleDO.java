package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class HttpApplicationRuleDO extends SimpleBaseDO {

    ApplicationInformationBO application;
    @Id
    private String id;
    private String key;
    private String mode;
    private String value;

}
