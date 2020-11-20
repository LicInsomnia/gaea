package com.tincery.gaea.producer.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author gongxuanzhang
 */
@Component
@Slf4j
public class SrcProducer extends AbstractProducer {


    @Value("${node.src-path}")
    private String srcPath;

    @Override
    public File getRootFile(String category, String extension) {
        return new File(srcPath + "/" + category + "/");
    }

    @Override
    @Autowired
    public void setJmsMessagingTemplate(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

}
