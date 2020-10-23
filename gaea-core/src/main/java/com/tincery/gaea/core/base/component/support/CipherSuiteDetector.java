package com.tincery.gaea.core.base.component.support;


import com.tincery.gaea.api.base.CipherSuiteDO;
import com.tincery.gaea.core.base.dao.CipherSuiteDao;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gxz
 * 加载sys.payload_detector生成载荷应用检测器
 */
@Component
@Slf4j
public class CipherSuiteDetector implements InitializationRequired {


    private final Map<String, CipherSuiteDO> cipherSuiteConfigMap = new HashMap<>();
    @Autowired
    private CipherSuiteDao cipherSuiteDao;

    public CipherSuiteDO getCipherSuite(String id) {
        CipherSuiteDO cipherSuite = new CipherSuiteDO();
        if (!this.cipherSuiteConfigMap.containsKey(id)) {
            cipherSuite.setId(id);
            return cipherSuite;
        }
        return this.cipherSuiteConfigMap.get(id);
    }

    @Override
    public void init() {
        List<CipherSuiteDO> cipherSuiteConfigList = this.cipherSuiteDao.findAll();
        for (CipherSuiteDO cipherSuiteDO : cipherSuiteConfigList) {
            this.cipherSuiteConfigMap.put(cipherSuiteDO.getId(), cipherSuiteDO);
        }
    }


}
