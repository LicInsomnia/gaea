
package com.tincery.gaea.source.email.execute;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ImpTargetSetupDO;
import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.core.base.component.*;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gxz
 */


@Component
@Slf4j
public class EmailLineAnalysis implements LineAnalysis<EmailData>, InitializationRequired {


    protected Map<String, String> target2Group = new HashMap<>();

    private final PayloadDetector payloadDetector;

    private final ImpTargetSetupDao impTargetSetupDao;

    private final ApplicationProtocol applicationProtocol;

    @Autowired
    private GroupGetter groupGetter;

    private final IpChecker ipChecker;

    public EmailLineAnalysis(PayloadDetector payloadDetector, ImpTargetSetupDao impTargetSetupDao, ApplicationProtocol applicationProtocol, IpChecker ipChecker) {
        this.payloadDetector = payloadDetector;
        this.impTargetSetupDao = impTargetSetupDao;
        this.applicationProtocol = applicationProtocol;
        this.ipChecker = ipChecker;
    }

    /****
     * @author gxz
     * @param line 一行json
     **/

    @Override
    public EmailData pack(String line) {
        JSONObject jsonObject = JSON.parseObject(line);
        EmailData emailData = jsonObject.toJavaObject(EmailData.class);
        String key = emailData.getProtocol() + "_" + emailData.getServerPort();
        emailData.setProName(applicationProtocol.getProNameOrDefault(key, "other"));
        emailData.setTargetName(emailData.getTargetName());
        emailData.setGroupName(this.groupGetter.getGroupName(emailData.getTargetName()));
        emailData.setForeign(ipChecker.isForeign(emailData.getServerIp()));
        if (emailData.getDataType() == -1) {
            emailData.setProName(payloadDetector.getProName(emailData));
        } else if (emailData.getDataType() == 1) {
            if (jsonObject.containsKey("rcpt_to")) {
                List<String> rcptTo = jsonObject.getJSONArray("rcpt_to").toJavaList(String.class);
                emailData.setRcpt(rcptTo);
            }
        }
        if (emailData.getDataType() == 1) {
            List<EmailData.Communication> emaildata = emailData.getEmailDataList();
            for (EmailData.Communication communication : emaildata) {
                List<Document> attach = communication.getAttach();
                if (!CollectionUtils.isEmpty(attach)) {
                    List<String> attachSuffixList = emailData.getAttachSuffixList();
                    if (attachSuffixList == null) {
                        attachSuffixList = new ArrayList<>();
                    }
                    attach.stream().map((document -> document.getString("attach_name")))
                            .map(FileUtils::getSuffix).forEach(attachSuffixList::add);
                    emailData.setAttachSuffixList(attachSuffixList);
                }
            }
        }
        emailData.adjust();
        return emailData;
    }


    @Override
    public void init() {
        List<ImpTargetSetupDO> activityData = impTargetSetupDao.getActivityData();
        activityData.stream()
                .filter(impTargetSetupDO -> StringUtils.notAllowNull(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()))
                .forEach((impTargetSetupDO) -> this.target2Group.put(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()));
        log.info("加载了{}组  目标配置", this.target2Group.size());
    }
}
