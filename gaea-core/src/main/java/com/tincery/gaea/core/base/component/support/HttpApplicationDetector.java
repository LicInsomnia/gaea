package com.tincery.gaea.core.base.component.support;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.HttpApplicationRuleDO;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.core.base.dao.HttpApplicationRuleDao;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HttpApplicationDetector implements InitializationRequired {

    @Autowired
    private HttpApplicationRuleDao httpApplicationRuleDao;

    private List<HttpApplicationRuleDO> httpDetectorList = new ArrayList<>();

    private ApplicationInformationBO detect(JSONObject jsonObject, HttpApplicationRuleDO httpDetect) {
        if (!jsonObject.containsKey(httpDetect.getKey())) {
            return null;
        }
        switch (httpDetect.getMode()) {
            case "startwith":
                return jsonObject.getString(httpDetect.getKey()).startsWith(httpDetect.getValue()) ? httpDetect.getApplication() : null;
            case "endwith":
                return jsonObject.getString(httpDetect.getKey()).endsWith(httpDetect.getValue()) ? httpDetect.getApplication() : null;
            case "contain":
                return jsonObject.getString(httpDetect.getKey()).contains(httpDetect.getValue()) ? httpDetect.getApplication() : null;
            default:
                return null;
        }
    }

    public ApplicationInformationBO detect(SessionMergeData sessionMergeData) {
        JSONObject jsonObject = sessionMergeData.getExtension();
        if (null == jsonObject) {
            return null;
        }
        for (HttpApplicationRuleDO httpDetect : httpDetectorList) {
            ApplicationInformationBO application = detect(jsonObject, httpDetect);
            if (null != application) {
                return application;
            }
        }
        return null;
    }

    @Override
    public void init() {
        this.httpDetectorList = this.httpApplicationRuleDao.findAll();
    }
}
