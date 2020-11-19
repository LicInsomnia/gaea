package com.tincery.gaea.api.base;


import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

/**
 * @author gxz
 */
@Data
public class CertDo extends SimpleBaseDO {
    @Id
    private String id;
    private Integer alertcheck;
    private Integer altnamedganum;
    private Integer altnamenum;
    private Integer altnamewhitenum;
    private String captime;
    private Long captime_n;
    private String[] casetags;
    private Integer complete;
    private Integer compliance;
    private String[] compliancedetail;
    private String[] compliancetags;
    private Integer compliancetype;
    private Integer gmcompliancetype;
    private String[] gmcompliancedetail;
    private String[]  gmcompliancetags;
    private String ex_authinfoaccesssyntax;
    private String ex_basicconstraints;
    private String ex_certificatepolicies;
    private String ex_crldistributionpoints;
    private String ex_keyusage;
    private String ex_subjectaltname;
    private String ex_subjectkeyidentifier;
    private Integer index;
    private String inserttime;
    private Long inserttime_n;
    private String issuer_cn;
    private String issuer_c;
    private String issuer_l;
    private String issuer_o;
    private String issuer_s;
    private Double reliability;
    private String[] reliabilitydetail;
    private String[] reliabilitytags;
    private Integer reliabilitytype;
    private String rsa_e;
    private String rsa_n;
    private String ecc_r;
    private String ecc_s;
    private Integer selfsigned;
    private String serialnum;
    private String signaturealgo;
    private String signaturealgooid;
    private String source;
    private String subject_cn;
    private String subject_c;
    private String subject_l;
    private String subject_o;
    private String subject_ou;
    private String subject_s;
    private String subjectpublickey;
    private String subjectpublickeyalgo;
    private String subjectpublickeyalgooid;
    private Integer subjectpublickeylength;
    private Long validafter;
    private Long validbefore;
    private Long validlength;
    private Integer version;
    private String encrypted;
    private Integer malicious_website;
}
