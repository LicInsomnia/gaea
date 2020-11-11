package com.tincery.gaea.producer.producer;

import javax.jms.Queue;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@FunctionalInterface
public interface Producer {

    void producer(Queue queue, String category, String extension);
}
