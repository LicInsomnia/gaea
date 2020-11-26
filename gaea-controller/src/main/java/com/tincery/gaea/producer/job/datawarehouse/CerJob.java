package com.tincery.gaea.producer.job.datawarehouse;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.producer.DwProducer;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import javax.annotation.Resource;
import javax.jms.Queue;

/**
 * @author lbz liubangzhou@163.com
 **/
@Slf4j
public class CerJob extends QuartzJobBean {
    @Resource(name = QueueNames.DW_CER)
    Queue cerQueue;

    @Autowired
    private DwProducer dwProducer;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        dwProducer.producer(cerQueue, null, null);
        log.info("发送了一条数据");
    }
}
