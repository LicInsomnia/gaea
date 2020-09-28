package com.tincery.gaea.datawarehouse.reorganization.job;

import com.tincery.gaea.core.base.component.Execute;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ReorganizationJob extends QuartzJobBean {
    @Autowired
    private Execute execute;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        execute.execute();
    }
}
