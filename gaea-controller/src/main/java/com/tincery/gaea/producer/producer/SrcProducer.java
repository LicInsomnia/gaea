package com.tincery.gaea.producer.producer;

import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.jms.Queue;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@Setter
public class SrcProducer {


    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${node.src-path}")
    private String srcPath;

    private Map<String,Long> categoryLong = new HashMap<>();

    public void producer(Queue queue, String category, String extension) {
        File path = new File(srcPath + "/" + category + "/");
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
        long lastTime = categoryLong.getOrDefault(category,0L);
        List<String> files = allFiles.stream()
                .sorted(Comparator.comparingLong(File::lastModified)).filter(file -> file.lastModified() >= lastTime).map(File::getAbsolutePath)
                .collect(Collectors.toList());
        if (!files.isEmpty()) {
            File lastFile = new File(files.get(files.size() - 1));
            categoryLong.put(category,lastFile.lastModified());
            for (String file : files) {
                this.jmsMessagingTemplate.convertAndSend(queue, file);
            }
            log.info("本次处理从[{}]目录中共获取{}[{}]个 忽略[{}]个 入队[{}]个", path, category, allFiles.size(),
                    (allFiles.size() - files.size()), files.size());
        } else {
            log.info("本次处理从[{}]目录中 获取到[{}]个文件 全部忽略了", path, allFiles.size());
        }

    }

    public static void main(String[] args) throws IOException {
        File file = new File("aaa.txt");
        boolean newFile = file.createNewFile();
        System.out.println(file.lastModified());


    }

}
