package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.base.component.config.NodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class EnvInit implements ApplicationListener<ContextRefreshedEvent> {



    private static final String NAME = "node.name";

    private static final String HOME = "node.home";

    private static final String CATEGORY = "node.category";



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String category = environment.getProperty(CATEGORY);
        String home = environment.getProperty(HOME);
        String name = environment.getProperty(NAME);
        NodeInfo.init(name,category,home);
    }

}
