package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.DpiDetectorDO;
import com.tincery.gaea.api.base.DpiInformation;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.core.base.dao.DpiDetectorDao;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Insomnia
 */
@Slf4j
@Component
public class DpiProtocolDetector implements InitializationRequired {

    private final Map<String, Map<String, ApplicationInformationBO>> dpiApplication = new HashMap<>();

    @Autowired
    private DpiDetectorDao dpiDetectorDao;

    private ApplicationInformationBO detect(String category, int protocol, int serverPort) {
        if (!this.dpiApplication.containsKey(category)) {
            return null;
        }
        String key = protocol + "_" + serverPort;
        Map<String, ApplicationInformationBO> map = this.dpiApplication.get(category);
        ApplicationInformationBO application = map.getOrDefault(key, null);
        if (null == application) {
            return map.get("default");
        }
        return application;
    }

    public ApplicationInformationBO detect(SessionMergeData sessionMergeData) {
        return detect(sessionMergeData.getDataSource(), sessionMergeData.getProtocol(), sessionMergeData.getServerPort());
    }

    @Override
    public void init() {
        List<DpiDetectorDO> dpiDetectorList = dpiDetectorDao.findAll();
        for (DpiDetectorDO dpiDetector : dpiDetectorList) {
            Map<String, ApplicationInformationBO> dpiMap = new HashMap<>();
            String id = dpiDetector.getId();
            List<DpiInformation> dpiInformations = dpiDetector.getDpiInformation();
            for (DpiInformation dpiInformation : dpiInformations) {
                String key = dpiInformation.getKey();
                ApplicationInformationBO application = dpiInformation.getApplication();
                dpiMap.put(key, application);
            }
            this.dpiApplication.put(id, dpiMap);
        }
    }

}
