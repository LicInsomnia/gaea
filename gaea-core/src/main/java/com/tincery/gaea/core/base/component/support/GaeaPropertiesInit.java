package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 环境初始化
 **/
@Slf4j
@ConfigurationProperties (prefix = "node")
@Setter
public class GaeaPropertiesInit implements ApplicationListener<ContextRefreshedEvent> {

    private String name;

    private String home;

    private String srcPath;

    private String dataPath;

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        NodeInfo.init(name, home, srcPath, dataPath);
        ApplicationInfo.init(environment.getProperty("spring.application.name"));
    }

}
