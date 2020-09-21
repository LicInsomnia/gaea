package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author gxz
 */
@Setter
@Getter
public class AppRule extends SimpleBaseDO {
    @Id
    private String id;
    private String title;
    private List<String> type;
    private List<String> specialtag;
    private String createuser;
}
