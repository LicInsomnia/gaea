package com.tincery.gaea.source.flow.execute;

import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.DnsData;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author gxz
 */
@Component
@Slf4j
@Setter
@Getter
@Service
public class FlowReceiver extends AbstractSrcReceiver<DnsData> {

    /**
     * 多线程实现执行
     * 如需要多线程实现 请重写此方法
     *
     * @author gxz
     **/
    @Override
    protected void analysisFile(File file) {
        if (!file.exists()) {
            return;
        }
        List<String> lines = FileUtils.readLine(file);
        if (lines.isEmpty()) {
            return;
        }
        int executor = this.properties.getExecutor();
        if (executor <= 1 || executor <= lines.size()) {
            analysisLine(lines);
        } else {
            List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);

            this.countDownLatch = new CountDownLatch(partitions.size());
            for (List<String> partition : partitions) {
                executorService.execute(() -> analysisLine(partition));
            }
        }
    }

    @Autowired
    public void setAnalysis(FlowLineAnalysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    @Override
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return null;
    }

    @Override
    public void init() {
        // loadGroup();
    }

}
