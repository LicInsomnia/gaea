package com.tincery.gaea.producer.job.src;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.config.SrcProducer;
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
public class ImpSessionJob extends QuartzJobBean {


    @Resource (name = QueueNames.SRC_IMPSESSION)
    private Queue impsessionQueue;
    @Autowired
    private SrcProducer srcProducer;


    @Override
    protected void executeInternal(JobExecutionContext context) {
            this.srcProducer.producer(this.impsessionQueue, "impsession", ".txt");
    }
}
