package com.tincery.gaea.source.session.quartz;

import com.tincery.gaea.core.base.component.Execute;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SessionJob extends QuartzJobBean {
    @Autowired
    private Execute sessionExecute;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        sessionExecute.execute();
    }
}
