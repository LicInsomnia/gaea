package com.tincery.gaea.source.email.execute;


import com.tincery.gaea.api.base.ImpTargetSetupDO;
import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.GroupGetter;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.dao.ImpTargetSetupDao;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author gxz
 */


@Component
@Slf4j
public class EmailLineAnalysis implements SrcLineAnalysis<EmailData>, InitializationRequired {


    private final PayloadDetector payloadDetector;
    private final ImpTargetSetupDao impTargetSetupDao;
    private final ApplicationProtocol applicationProtocol;
    private final IpChecker ipChecker;
    protected Map<String, String> target2Group = new HashMap<>();
    @Autowired
    private GroupGetter groupGetter;
    @Autowired
    private SrcLineSupport srcLineSupport;

    public EmailLineAnalysis(PayloadDetector payloadDetector, ImpTargetSetupDao impTargetSetupDao, ApplicationProtocol applicationProtocol, IpChecker ipChecker) {
        this.payloadDetector = payloadDetector;
        this.impTargetSetupDao = impTargetSetupDao;
        this.applicationProtocol = applicationProtocol;
        this.ipChecker = ipChecker;
    }


    /**
     * 0.emlName	1.loginUser	2.loginPass	3.proper（0-登录失败，1-成功）
     * 4.iDirect(31-收，32-发，02-登录)
     * 手机5.cImsi	6.cImei	7.cMsisdn √
     * 8.client_ip_n 9.client_ipV6	10.serverIp_n 11.serverIpV6 √
     * 12.clientPort 13.serverPort	14.StartTick √
     * 15.sender 16.rcptTo
     * 17.clientMac 18.serverMac √
     * 19.syn 20.fin √
     * 21.endTime √
     * FLOW: 22.uppkt 23.upbyte 24.downpkt 25.downbyte √
     * 26. datatype(1:正常 -1：mainform伪造) √
     * 27.protocol √
     * 28.source:哪个探针来的 29.ruleName：重点目标名称 √
     * 30.outclientip 31.outserverip 32.outclientport 33.outserverport 34.outproto √
     * 35.userid 36.serverid √
     * 37.ismac2outer √
     * 38.upPayload 39.downPayload √
     * 40.ifImapPart
     * @param line
     * @return
     */
    @Override
    public EmailData pack(String line) {
        EmailData emailData = new EmailData();

        String[] elements = line.split("\t");

        fixCommon(emailData,elements);

        if (emailData.getDataType() == -1){
            fixMalformed(emailData,elements);
        }
        fixNormal(emailData,elements);

        return emailData;
    }

    /**
     * 设置其他值
     * @param emailData 实体
     * @param elements 源
     */
    private void fixNormal(EmailData emailData, String[] elements) {
        emailData.setEmlName(elements[0])
                .setLoginUser(elements[1])
                .setLoginPass(elements[2])
                .setProper(Integer.parseInt(elements[3]))
                .setIDirect(Integer.parseInt(elements[4]))
                .setSender(elements[15])
                .setRcptTo(elements[16])
                .setIfImapPart(elements[40]);
    }

    /**
     * 设置malformed 的值
     * @param emailData  实体
     * @param elements 源
     */
    private void fixMalformed(EmailData emailData, String[] elements) {
        srcLineSupport.setMalformedPayload(elements[38],elements[39],emailData);
    }

    /**
     * 填充Common属性
     * @param emailData 邮件实体
     * @param elements 源
     */
    private void fixCommon(EmailData emailData, String[] elements) {
        srcLineSupport.setMobileElements(elements[5],elements[6],elements[7],emailData);
        srcLineSupport.set7Tuple(elements[18],elements[17],
                choiceString(elements[10],elements[11]),
                choiceString(elements[8],elements[9]),elements[13],elements[12],elements[27],
                "EMAIL",emailData);
        srcLineSupport.setSynAndFin(elements[19],elements[20],emailData);
        srcLineSupport.setTime(elements[14],elements[21],emailData);
        srcLineSupport.setFlow(elements[22],elements[23],elements[24],elements[25],emailData);
        emailData.setDataType(Integer.parseInt(elements[26]))
                .setSource(elements[28]);
        srcLineSupport.setTargetName(elements[29],emailData);
        srcLineSupport.setGroupName(emailData);
        srcLineSupport.set5TupleOuter(elements[30],elements[31],elements[32],elements[33],elements[34],emailData);
        srcLineSupport.setPartiesId(elements[35],elements[36],emailData);
        srcLineSupport.setIsMac2Outer(elements[37],emailData);
    }

    private String choiceString(String ipv4,String ipv6){
        if (Objects.isNull(ipv4)){
            return ipv6;
        }
        return ipv4;
    }


    @Override
    public void init() {
        List<ImpTargetSetupDO> activityData = impTargetSetupDao.getActivityData();
        activityData.stream()
                .filter(impTargetSetupDO -> StringUtils.notAllowNull(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()))
                .forEach((impTargetSetupDO) -> this.target2Group.put(impTargetSetupDO.getTargetname(), impTargetSetupDO.getGroupname()));
        log.info("加载了{}组目标配置", this.target2Group.size());
    }

}
