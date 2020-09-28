package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author gxz
 */
@Data
public class CertDo extends SimpleBaseDO {
    @Id
    private String id;
    private Integer compliance;
    private Integer compliancetype;
    private Integer reliability;
    private Integer reliabilitytype;
    private Integer selfsigned;
    private String subject_cn;
    private String ex_subjectaltname;
    private String sha1;

}
