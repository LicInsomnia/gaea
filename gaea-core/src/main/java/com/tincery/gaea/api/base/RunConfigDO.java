package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Setter
@Getter
public class RunConfigDO extends SimpleBaseDO {
    @Id
    private String id;
    private Integer threadovertime;
    private Integer threadwaittime;

}
