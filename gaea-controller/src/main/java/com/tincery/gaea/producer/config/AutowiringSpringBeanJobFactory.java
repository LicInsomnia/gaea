package com.tincery.gaea.producer.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/***
 * SpringBoot集成Quartz的时候 会出现Spring容器没加载完成 但是定时任务已经开始执行的情况
 * 因为定时任务需要依赖Spring容器中的内容  所以就会出现空指针的情况
 * 此类就是为了解决如上问题
 * @author gongxuanzhang
 */

@Component
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
        ApplicationContextAware {

    @Autowired
    private transient AutowireCapableBeanFactory autowireCapablebeanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        autowireCapablebeanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        autowireCapablebeanFactory.autowireBean(job);
        return job;
    }
}
