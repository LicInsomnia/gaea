package com.tincery.gaea.datamarket.sessionadjust.execute;


import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.support.*;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.datamarket.sessionadjust.support.ContextCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Insomnia
 */
@Slf4j
@Component
public class SessionAdjustFactory {

    @Autowired
    ApplicationCheck applicationCheck;
    @Autowired
    ApplicationProtocol applicationProtocol;
    @Autowired
    PayloadDetector payloadDetector;
    @Autowired
    HttpApplicationDetector httpApplicationDetector;
    @Autowired
    DpiProtocolDetector dpiProtocolDetector;
    @Autowired
    ContextCache contextCache;

    private void addApplicationElements(ApplicationInformationBO application, Map<String, ApplicationInformationBO> applicationElements, String mode) {
        if (null == application) {
            return;
        }
        applicationElements.put(mode, application);
    }

    /**
     * 检测会话数据所有应用可能性
     *
     * @param data 会话数据
     * @return 检测模式 -> 应用对照表
     */
    private Map<String, ApplicationInformationBO> applicationDetect(SessionMergeData data) {
        Map<String, ApplicationInformationBO> applicationElements = new HashMap<>(11);
        ApplicationInformationBO application;
        /* Http特殊字段会话应用识别 */
        application = httpApplicationDetector.detect(data);
        addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.HTTP_DETECTOR_STRING);
        /* 基于关键词的会话应用识别 */
        application = applicationCheck.getApplicationInformation(data.getKeyWord());
        addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_STRING);
        /* 基于载荷的会话应用识别 */
        application = payloadDetector.getApplication(data);
        addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.PAYLOAD_STRING);
        /* 关键词直接转义 */
        if (null != data.getKeyWord()) {
            List<String> type = new ArrayList<>();
            type.add("综合其它@综合其它");
            application = new ApplicationInformationBO();
            application.setTitle(data.getKeyWord());
            application.setType(type);
            application.setIgnore(false);
            addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_TO_APP_STRING);
        }
        /* 基于证书的会话应用识别 */
        JSONObject cer = data.getCer();
        if (null != cer) {
            Object subjectCn = cer.getOrDefault(HeadConst.FIELD.SUBJECT_CN_STRING, null);
            application = applicationCheck.getApplicationInformation(subjectCn);
            addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.CER_STRING);
        }
        /* 基于会话协议端口的会话应用识别 */
        if (null == data.getMalFormed() || !data.getMalFormed()) {
            String appKey4Protocol = data.getProtocol() + "_" + data.getServerPort();
            application = applicationProtocol.getApplication(appKey4Protocol);
            addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.PROTOCOL_STRING);
        }
//        /* 基于动态挖掘标签的会话应用识别 */
//        Map<String, Object> appByLabel = labelAppClue.getOrDefault(this._id, null);
//        if (addApplicationElements(appByLabel, applicationElements, HeadConst.APPLICATION_DETECT_MODE.DYNAMIC_STRING)) {
//            labelAppClue.remove(this._id);
//        }
        /* 基于上下文的会话应用识别 */
        application = contextCache.detect(data);
        addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.CONTEXT_STRING);
        /* 基于前序DNS请求的会话应用识别 */
        if (null != data.getDnsRequestBO()) {
            application = applicationCheck.getApplicationInformation(data.getDnsRequestBO().getDomain());
            addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.DNSREQUEST_STRING);
        }
        /* 基于数据来源的DPI应用识别 */
        if (null == data.getMalFormed() || !data.getMalFormed()) {
            application = dpiProtocolDetector.detect(data);
            addApplicationElements(application, applicationElements, HeadConst.APPLICATION_DETECT_MODE.DPI_STRING);
        }
        return applicationElements;
    }

    public SessionMergeData adjustSessionData(AbstractDataWarehouseData data) {
        SessionMergeData sessionMergeData = new SessionMergeData(data);
        /* 检测所有应用可能性 */
        Map<String, ApplicationInformationBO> applicationElements = applicationDetect(sessionMergeData);
        sessionMergeData.setApplicationElements(applicationElements);
        /* 按优先级标识应用 */
        applicationAdjust(applicationElements, sessionMergeData);
        return sessionMergeData;
    }

    /**
     * 按优先级标识应用
     *
     * @param applicationElements 检测模式 -> 应用对照表
     * @param sessionMergeData    会话数据
     */
    private void applicationAdjust(Map<String, ApplicationInformationBO> applicationElements, SessionMergeData sessionMergeData) {
        if (CollectionUtils.isEmpty(applicationElements)) {
            return;
        }
        ApplicationInformationBO application = null;
        String checkMode = null;
        if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.PAYLOAD_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.PAYLOAD_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.PAYLOAD_STRING;
            if (!sessionMergeData.getProName().equals(HeadConst.PRONAME.DNS)) {
                this.contextCache.append(sessionMergeData.targetSessionKey(), application);
            }
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.DYNAMIC_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.DYNAMIC_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.DYNAMIC_STRING;
            if (!sessionMergeData.getProName().equals(HeadConst.PRONAME.DNS)) {
                this.contextCache.append(sessionMergeData.targetSessionKey(), application);
            }
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.HTTP_DETECTOR_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.HTTP_DETECTOR_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.HTTP_DETECTOR_STRING;
            this.contextCache.append(sessionMergeData.targetSessionKey(), application);
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_STRING;
            if (!sessionMergeData.getProName().equals(HeadConst.PRONAME.DNS)) {
                this.contextCache.append(sessionMergeData.targetSessionKey(), application);
            }
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_TO_APP_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_TO_APP_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.KEYOWRD_TO_APP_STRING;
            if (!sessionMergeData.getProName().equals(HeadConst.PRONAME.DNS)) {
                this.contextCache.append(sessionMergeData.targetSessionKey(), application);
            }
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.CER_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.CER_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.CER_STRING;
            if (!sessionMergeData.getProName().equals(HeadConst.PRONAME.DNS)) {
                this.contextCache.append(sessionMergeData.targetSessionKey(), application);
            }
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.DPI_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.DPI_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.DPI_STRING;
            this.contextCache.append(sessionMergeData.targetSessionKey(), application);
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.CONTEXT_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.CONTEXT_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.CONTEXT_STRING;
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.SERVERIP_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.SERVERIP_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.SERVERIP_STRING;
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.DPI_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.DPI_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.DPI_STRING;
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.DNSREQUEST_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.DNSREQUEST_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.DNSREQUEST_STRING;
        } else if (applicationElements.containsKey(HeadConst.APPLICATION_DETECT_MODE.PROTOCOL_STRING)) {
            application = applicationElements.get(HeadConst.APPLICATION_DETECT_MODE.PROTOCOL_STRING);
            checkMode = HeadConst.APPLICATION_DETECT_MODE.PROTOCOL_STRING;
        }
        /* 如果应用不属于任何一种模式或应用本身ignore == true则不进行应用标记 */
        if (null == application || application.getIgnore()) {
            return;
        }
        sessionMergeData.setApplication(application);
        sessionMergeData.setCheckMode(checkMode);
    }

}
