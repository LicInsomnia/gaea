package com.tincery.gaea.core.base.component;

import com.tincery.starter.base.InitializationRequired;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

public interface Receiver extends InitializationRequired {

    int CPU = Runtime.getRuntime().availableProcessors();

    /*****
     * 执行器核心方法 具体执行内容
     * @author gxz
     **/
    void receive(TextMessage textMessage) throws JMSException;

}
