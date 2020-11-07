package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Setter
@Getter
public class Location implements Serializable {
    private String country;
    private String countryZh;
    private String region;
    private String regionZh;
    private String city;
    private String cityZh;
    private Double lng;
    private Double lat;
    private String organization;
    private String connectionType;
    private String isp;
    private String type;
    private String autonomousSystemOrganization;
    private String cloudService;
}
