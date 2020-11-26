package com.tincery.gaea.core.base.tool.moduleframe;

import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.starter.base.model.SimpleBaseDO;
import org.bson.Document;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author liuming
 */
public class DataQueue {
    private String tag;
    private Integer copyNum = 0;
    private Integer capacity = 256;
    private BlockingQueue<AbstractMetaData> queue = new ArrayBlockingQueue<>(capacity);

    public DataQueue(String tag) {
        this.tag = tag;
    }

    synchronized public void attach() {
        copyNum++;
    }

    synchronized public void detach() {
        copyNum--;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getTag() {
        return tag;
    }

    public AbstractMetaData poll() {
        try {
            return queue.poll();
        } catch (Exception e) {
            return null;
        }
    }

    public AbstractMetaData poll(long timeout, TimeUnit timeUnit) {
        try {
            return queue.poll(timeout, timeUnit);
        } catch (Exception e) {
            return null;
        }
    }


    public void put(AbstractMetaData doc) {
        try {
            queue.put(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer size() {
        return queue.size();
    }

    public boolean isEnd() {
        return copyNum == 0 && queue.size() == 0;
    }
}
