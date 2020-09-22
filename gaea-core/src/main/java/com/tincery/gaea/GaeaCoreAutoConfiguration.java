package com.tincery.gaea;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Configuration
@ComponentScans(
        {@ComponentScan("com.tincery.gaea.core.*")
        })
public class GaeaCoreAutoConfiguration {


}
