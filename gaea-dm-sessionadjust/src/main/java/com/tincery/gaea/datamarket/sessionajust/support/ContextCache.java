package com.tincery.gaea.datamarket.sessionajust.support;

import com.alibaba.fastjson.JSON;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.dm.SessionMergeData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Insomnia
 */
@Component
public class ContextCache implements InitializationRequired {

    private final Map<String, ApplicationInformationBO> map = new ConcurrentHashMap<>();

    @Override
    public void init() {
        String historyPath = ApplicationInfo.getDataMarketBakByCategory();
        File file = FileUtils.getLastFile(historyPath, null, null, ".json");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                SessionMergeData data = JSON.parseObject(line, SessionMergeData.class);
                this.map.put(data.targetSessionKey(), data.getApplication());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(SessionMergeData data) {

    }

}
