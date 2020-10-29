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
public class ConstantComparisonTableDO extends SimpleBaseDO {
    @Id
    public String id;
    private List<Contrast> contrast;

    @Setter
    @Getter
    public static class Contrast {
        private String key;
        private List<String> code;
    }
}
