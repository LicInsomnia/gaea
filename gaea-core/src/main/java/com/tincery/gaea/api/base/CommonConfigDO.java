package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

@Data
@Document(collection = "common_config")
public class CommonConfigDO extends SimpleBaseDO {
    @Id
    private String id;
    private Object value;
    private String description;
}
