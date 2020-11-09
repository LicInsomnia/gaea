package com.tincery.gaea.producer.producer;

import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Setter
public class SrcProducer {

    private long currentTime = 0L;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${node.src-path}")
    private String srcPath;

    private LocalDateTime preTime;

    public void producer(Queue queue, String category, String extension) {
        File path = new File(srcPath + "/" + category + "/");
        if (!path.exists()) {
            return;
        }
        long delayTime = 0;
        if (preTime != null) {
            Duration between = Duration.between(preTime, LocalDateTime.now());
            // 这里预留60秒 防止扫描过程中出现或者删除的文件
            delayTime = between.getSeconds()+60;
        }
        preTime = LocalDateTime.now();
        List<String> files = FileUtils.searchFiles(path.getAbsolutePath(),
                category,
                null,
                extension,
                delayTime)
                .stream()
                .sorted(Comparator.comparingLong(File::lastModified)).map(File::getAbsolutePath)
                .collect(Collectors.toList());
        for (String file : files) {
            this.jmsMessagingTemplate.convertAndSend(queue, file);
        }
        log.info("本次处理从[{}]目录中共获取{} SRC文件{}个，并全部生产", path, category, files.size());
    }

}
