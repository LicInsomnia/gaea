package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Setter
@Getter
public class AppDetect extends SimpleBaseDO {
    @Id
    private String id;

    List<SearchCondition> conditions;

    private String description;

}
