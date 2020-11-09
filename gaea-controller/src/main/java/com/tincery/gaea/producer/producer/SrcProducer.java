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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Setter
public class SrcProducer {


    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${node.src-path}")
    private String srcPath;

    private long lastTime;

    public void producer(Queue queue, String category, String extension) {
        File path = new File(srcPath + "/" + category + "/");
        if (!path.exists()) {
            return;
        }
        List<String> files = FileUtils.searchFiles(path.getAbsolutePath(),
                category,
                null,
                extension,
                0L)
                .stream()
                .sorted(Comparator.comparingLong(File::lastModified)).filter(file -> file.lastModified() >= lastTime).map(File::getAbsolutePath)
                .collect(Collectors.toList());
        File lastFile = new File(files.get(files.size()-1));
        lastTime = lastFile.lastModified();
        for (String file : files) {
            this.jmsMessagingTemplate.convertAndSend(queue, file);
        }
        log.info("本次处理从[{}]目录中共获取{} SRC文件{}个，并全部生产", path, category, files.size());
    }

}
