package com.tincery.gaea.producer.job.datamarket;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.producer.DmAlarmProducer;
import com.tincery.gaea.producer.producer.DmProducer;
import com.tincery.gaea.producer.producer.DwProducer;
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
public class AlarmCombineJob extends QuartzJobBean {

    @Resource(name = QueueNames.DM_ALARMCOMBINE)
    Queue alarmCombineQueue;

    @Autowired
    private DmAlarmProducer dmAlarmProducer;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        dmAlarmProducer.producer(this.alarmCombineQueue,"alarmMaterial",".json");
        log.info("发送了一条数据");
    }
}
