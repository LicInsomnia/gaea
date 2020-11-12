package com.tincery.gaea.core.dm;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public abstract class AbstractDataMarketReceiver implements Receiver {


    protected DmProperties dmProperties;

    protected abstract void setDmProperties(DmProperties dmProperties);

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
        if (!file.exists()) {
            log.warn("{}已经被处理", file.getPath());
            return;
        }
        List<String> allLines = FileUtils.readLine(file);
        dmFileAnalysis(allLines);
    }

    protected abstract void dmFileAnalysis(List<String> lines);

    protected void freeFile(File file) {
        /* 删除或备份原始文件 */
        if (this.dmProperties.isBack()) {
            String src = file.getAbsolutePath();
            String dst = ApplicationInfo.getDataMarketBakByCategory() + "/" + file.getName();
            FileUtils.fileMove(src, dst);
        } else {
            if (!file.delete()) {
                log.error("删除文件[{}]失败", file.getAbsolutePath());
            }
        }
    }

}