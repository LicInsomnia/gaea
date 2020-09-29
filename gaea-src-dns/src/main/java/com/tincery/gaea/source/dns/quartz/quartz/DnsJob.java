package com.tincery.gaea.source.dns.quartz.quartz;

import com.tincery.gaea.core.base.component.Execute;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class DnsJob extends QuartzJobBean {
    @Autowired
    private Execute dnsExecute;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        dnsExecute.execute();
    }
}
