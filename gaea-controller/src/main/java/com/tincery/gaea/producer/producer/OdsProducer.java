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
public class OdsProducer {

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${node.data-path}")
    private String dataPath;

    public static void main(String[] args) {

    }

    public void producer(Queue queue, String category, String extension) {
        File path = new File(dataPath + "/cache/" + category);
        if (!path.exists()) {
            log.info("扫描路径{}不存在", path.getAbsoluteFile());
            return;
        }
        List<String> files = FileUtils.searchFiles(path.getAbsolutePath(),
                category,
                null,
                extension,
                0)
                .stream()
                .sorted(Comparator.comparingLong(File::lastModified)).map(File::getAbsolutePath)
                .collect(Collectors.toList());
        for (String file : files) {
            this.jmsMessagingTemplate.convertAndSend(queue, file);
        }
        log.info("本次处理从[{}]目录中共获取{}文件{}个，并全部生产", path, category, files.size());

    }

}
