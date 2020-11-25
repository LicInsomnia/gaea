package com.tincery.gaea.producer.config;

import com.tincery.gaea.api.base.QueueNames;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;


/**
 * @author gxz gongxuanzhang@foxmail.com
 * ActiveMq 被注入的queue
 **/
@Configuration
public class ActiveMqConfig {

    @Bean(name = QueueNames.SRC_FLOW)
    public Queue getSrcFlowQueue() {
        return new ActiveMQQueue(QueueNames.SRC_FLOW);
    }

    @Bean(name = QueueNames.SRC_SESSION)
    public Queue getSrcSessionQueue() {
        return new ActiveMQQueue(QueueNames.SRC_SESSION);
    }

    @Bean(name = QueueNames.SRC_IMPSESSION)
    public Queue getSrcImpSession() {
        return new ActiveMQQueue(QueueNames.SRC_IMPSESSION);
    }

    @Bean(name = QueueNames.SRC_SSL)
    public Queue getSrcSsl() {
        return new ActiveMQQueue(QueueNames.SRC_SSL);
    }

    @Bean(name = QueueNames.SRC_OPENVPN)
    public Queue getSrcOpenVpn() {
        return new ActiveMQQueue(QueueNames.SRC_OPENVPN);
    }

    @Bean(name = QueueNames.SRC_DNS)
    public Queue getSrcDns() {
        return new ActiveMQQueue(QueueNames.SRC_DNS);
    }

    @Bean(name = QueueNames.SRC_HTTP)
    public Queue getSrcHttp() {
        return new ActiveMQQueue(QueueNames.SRC_HTTP);
    }

    @Bean(name = QueueNames.SRC_EMAIL)
    public Queue getSrcEmail() {
        return new ActiveMQQueue(QueueNames.SRC_EMAIL);
    }

    @Bean(name = QueueNames.DW_REORGANIZATION)
    public Queue getDwReorganization() {
        return new ActiveMQQueue(QueueNames.DW_REORGANIZATION);
    }

    @Bean(name = QueueNames.SRC_SSH)
    public Queue getSrcSsh() {
        return new ActiveMQQueue(QueueNames.SRC_SSH);
    }

    @Bean(name = QueueNames.SRC_PPTPANDL2TP)
    public Queue getPptpandl2tp() {
        return new ActiveMQQueue(QueueNames.SRC_PPTPANDL2TP);
    }

    @Bean(name = QueueNames.SRC_WECHAT)
    public Queue getWeChat() {
        return new ActiveMQQueue(QueueNames.SRC_WECHAT);
    }

    @Bean(name = QueueNames.SRC_ALARM)
    public Queue getAlarm() {
        return new ActiveMQQueue(QueueNames.SRC_ALARM);
    }

    @Bean(name = QueueNames.SRC_FTPANDTELNET)
    public Queue getFtpAndTelnet() {
        return new ActiveMQQueue(QueueNames.SRC_FTPANDTELNET);
    }

    @Bean(name = QueueNames.SRC_ESPANDAH)
    public Queue getEspAndAh() {
        return new ActiveMQQueue(QueueNames.SRC_ESPANDAH);
    }

    @Bean(name = QueueNames.SRC_ISAKMP)
    public Queue getIsakmp() {
        return new ActiveMQQueue(QueueNames.SRC_ISAKMP);
    }

    @Bean(name = QueueNames.ODS_HTTPANALYSIS)
    public Queue getHttpAnalysis() {
        return new ActiveMQQueue(QueueNames.ODS_HTTPANALYSIS);
    }

    @Bean(name = QueueNames.DM_ALARMCOMBINE)
    public Queue getAlarmCombine() {
        return new ActiveMQQueue(QueueNames.DM_ALARMCOMBINE);
    }

    @Bean(name = QueueNames.DM_ASSET)
    public Queue getAsset() {
        return new ActiveMQQueue(QueueNames.DM_ASSET);
    }

    @Bean(name = QueueNames.DM_SESSION_ADJUST)
    public Queue getSessionAdjust() {
        return new ActiveMQQueue(QueueNames.DM_SESSION_ADJUST);
    }

    @Bean(name = QueueNames.SUPPORT_MONGO_STASH)
    public Queue getSupportMongoStash() {
        return new ActiveMQQueue(QueueNames.SUPPORT_MONGO_STASH);
    }
}
