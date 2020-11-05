package com.tincery.gaea.producer.job.ods;

import com.tincery.gaea.api.base.QueueNames;
import com.tincery.gaea.producer.producer.HttpAnalysisProducer;
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
public class HttpAnalysisJob extends QuartzJobBean {


    @Resource(name = QueueNames.ODS_HTTPANALYSIS)
    private Queue httpAnalysisQueue;
    @Autowired
    private HttpAnalysisProducer httpAnalysisProducer;


    @Override
    protected void executeInternal(JobExecutionContext context) {
        this.httpAnalysisProducer.producer(this.httpAnalysisQueue, "http", ".json");
    }
}
