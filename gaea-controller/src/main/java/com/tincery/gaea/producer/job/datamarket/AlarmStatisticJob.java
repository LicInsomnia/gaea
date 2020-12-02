package com.tincery.gaea.producer.job.datamarket;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.producer.DmAlarmStatisticProducer;
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
public class AlarmStatisticJob extends QuartzJobBean {

    @Resource(name = QueueNames.DM_ALARM_STATISTIC)
    Queue alarmStatisticQueue;

    @Autowired
    private DmAlarmStatisticProducer dmAlarmStatisticProducer;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        dmAlarmStatisticProducer.producer(alarmStatisticQueue, null, null);
        log.info("发送了一条数据");
    }

}
