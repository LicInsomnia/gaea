package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author gxz
 */
@Data
public class CipherSuiteDO extends SimpleBaseDO {
    @Id
    private String id;

    /**
     * 密钥交换算法
     **/
    private String keyExchangeAlgorithm;

    /**
     * 实体认证数字签名算法
     **/
    private String authenticationAlgorithm;

    /**
     * 数据加密算法
     **/
    private String encryptionAlgorithm;

    /**
     * 完整性校验算法
     **/
    private String messageAuthenticationCodesAlgorithm;

}
