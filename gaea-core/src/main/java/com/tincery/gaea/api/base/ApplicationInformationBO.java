package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 应用实例化对象信息，apprule,httprule,applicationprotocolrule,payloadrule,dpidetector...
 *
 * @author gongxuanzhang
 */
@Setter
@Getter
@ToString
public class ApplicationInformationBO extends SimpleBaseDO {

    @Id
    private String id;

    private String key;

    private String title;

    private List<String> type;

    private List<String> specialTag;

    @Field (name = "isenc")
    private Boolean enc;

    private String proName;

    private Boolean ignore;



}
