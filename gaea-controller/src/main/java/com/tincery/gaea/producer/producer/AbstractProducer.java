package com.tincery.gaea.producer.producer;

import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.util.CollectionUtils;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public abstract class AbstractProducer implements Producer {


    protected Queue queue;
    protected JmsMessagingTemplate jmsMessagingTemplate;
    private final Map<String, Long> categoryLong = new HashMap<>();

    public void producer(Queue queue) {
        jmsMessagingTemplate.convertAndSend(queue, "dm任务");
        try {
            log.info("提交了一条dm.{}任务", queue.getQueueName());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void producer(Queue queue, String category, String extension) {
        File path = getRootFile(category, extension);
        if (!path.exists()) {
            return;
        }
        List<File> allFiles = FileUtils.searchFiles(path.getAbsolutePath(),
                category,
                null,
                extension,
                0L);
        if (CollectionUtils.isEmpty(allFiles)) {
            log.info("本次处理从[{}]目录中没有获取到文件", path);
            return;
        }
        long lastTime = this.categoryLong.getOrDefault(category, 0L);
        List<String> files = allFiles.stream()
                .sorted(Comparator.comparingLong(File::lastModified)).filter(file -> file.lastModified() >= lastTime).map(File::getAbsolutePath)
                .collect(Collectors.toList());
        if (!files.isEmpty()) {
            File lastFile = new File(files.get(files.size() - 1));
            categoryLong.put(category, lastFile.lastModified());
            for (String file : files) {
                this.jmsMessagingTemplate.convertAndSend(queue, file);
            }
            log.info("本次处理从[{}]目录中共获取{}[{}]个 忽略[{}]个 入队[{}]个,忽略{}时间之后的", path, category, allFiles.size(),
                    (allFiles.size() - files.size()), files.size(),DateUtils.Long2LocalDateTime(lastTime));
        } else {
            log.info("本次处理从[{}]目录中 获取到[{}]个文件 全部忽略了,忽略{}时间之后的", path, allFiles.size(),DateUtils.Long2LocalDateTime(lastTime));
        }
    }

    public abstract File getRootFile(String category, String extension);

    public abstract void setJmsMessagingTemplate(JmsMessagingTemplate jmsMessagingTemplate);

}
