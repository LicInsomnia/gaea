package com.tincery.gaea.producer.job.datamarket;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.producer.DmProducer;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import javax.jms.Queue;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class SessionAdjustJob extends QuartzJobBean {

    @Resource(name = QueueNames.DM_SESSION_ADJUST)
    Queue assetQueue;


    @Autowired
    private DmProducer dmProducer;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        dmProducer.producer(this.assetQueue, "impSessionMerge", ".json");
    }

}
