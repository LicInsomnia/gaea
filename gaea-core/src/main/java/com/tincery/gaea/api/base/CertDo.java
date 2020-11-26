package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;


/**
 * @author liuming
 */
@Data
public class CertDo extends SimpleBaseDO {
    @Id
    private String id;
    private Integer alertCheck;
    private Integer altnameDgaNum;
    private Integer altNameNum;
    private Integer altNameWhiteNum;
    private Long capTime;
    private String[] caseTags;
    private Integer complete;
    private Integer compliance;
    private String[] complianceDetail;
    private String[] complianceTags;
    private Integer complianceType;
    private Integer gmcomplianceType;
    private String[] gmcomplianceDetail;
    private String[]  gmcomplianceTags;
    private String exAuthorityInfoAccess;
    private String exBasicConstraints;
    private String exCertificatePolicies;
    private String exCRLDistributionPoints;
    private String exKeyUsage;
    private String exSubjectAltName;
    private String exSubjectKeyIdentifier;
    private String exAuthorityKeyIdentifier;
    private String exExtKeyUsage;
    private String exPrivateKeyUsagePeriod;
    private String exPolicyMappings;
    private String exIssuerAltName;
    private String exSubjectDirectoryAttributes;
    private String exNameConstraints;
    private String exPolicyConstraints;
    private String exInhibitanyPolicy;
    private String exFreshestCRL;
    private String exIdPkix;
    private String exSubjectInformationAccess;
    private String exIdentifyCardNumber;
    private String exInsuranceNumber;
    private String exICRegistrationNumber;
    private String exOrganizationCode;
    private String exTaxationNumber;
    private Integer index;
    private Long insertTime;
    private String issuerName;
    private String issuerCommonName;
    private String issuerCountryName;
    private String issuerLocalityName;
    private String issuerOrganizationName;
    private String issuerStateOrProvinceName;
    private String issuerOrganizationUnitName;
    private String issuerEmail;
    private String issuerDomainComponent;
    private Double reliability;
    private String[] reliabilityDetail;
    private String[] reliabilityTags;
    private Integer reliabilityType;
    private String rsaE;
    private String rsaN;
    private String eccR;
    private String eccS;
    private String eccX;
    private String eccY;
    private Integer selfSigned;
    private String serialNumber;
    private String signatureAlgorithm;
    private String signatureAlgorithmOid;
    private String source;
    private String subjectName;
    private String subjectCommonName;
    private String subjectCountryName;
    private String subjectLocalityName;
    private String subjectOorganizationName;
    private String subjectOrganizationUnitName;
    private String subjectStateOrProvinceName;
    private String subjectEmail;
    private String subjectDomainComponent;
    private String subjectPublicKeyInfoValue;
    private String subjectPublicKeyInfoAlgorithm;
    private String subjectPublicKeyInfoAlgorithmOid;
    private Integer subjectPublicKeyInfoLength;
    private Long validAfter;
    private Long validBefore;
    private Long validLength;
    private Integer version;
    private String encrypted;
    private Integer primalityDetectResult;
    private Integer smallFactorDetectResult;
    private Integer randomPrimeFactorDetectResult;
    private Integer rsaFixedPointNumberDetectResult;
    private Integer lowIndexAttackDetectResult;
    private Integer rsaSecurityStatus;
    private Integer keyExchangeLeakDetectResult;
    private Integer maliciousWebsite;
    private Boolean signatureCheck;
}
