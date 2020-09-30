package com.tincer.gaea.producer.config;

import org.quartz.*;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

public class DwQuartzConfig {


    private final String cron = "0 0/5 * * * ?";


    public JobDetail dataWarehouseJob() {
        return JobBuilder.newJob(DataWarehouseJob.class)
                .withIdentity("reorganizationJob")
                .storeDurably()
                .build();
    }


    public Trigger databaseMonitorJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        return TriggerBuilder.newTrigger()
                .forJob(dataWarehouseJob())
                .withIdentity("reorganizationJob")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
