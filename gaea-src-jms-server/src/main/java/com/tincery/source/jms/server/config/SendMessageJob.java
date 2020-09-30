package com.tincery.source.jms.server.config;

import com.alibaba.fastjson.JSON;
import com.tincery.gaea.api.base.AssetConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.jms.Queue;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class SendMessageJob extends QuartzJobBean {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;



    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        jmsMessagingTemplate.convertAndSend(queue, JSON.toJSON(new AssetConfigDO()).toString());
        log.info("发送了一条数据");
    }
}
