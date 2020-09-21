package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author gxz
 */
@Data
public class CloudConfigDO extends SimpleBaseDO {
    @Id
    private String id;

    private String region;

    private String service;
}
