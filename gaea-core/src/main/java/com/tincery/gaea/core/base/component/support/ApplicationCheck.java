package com.tincery.gaea.core.base.component.support;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.AppRuleDao;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.LevelDomainUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载sys.app_rule表中应用信息进行应用检测
 * 注意事项：
 * "exact" : true,表示可以对四级以上域名匹配，如果为false，默认只匹配到三级域名，例如mail.sina.com.cn
 * @author gongxuanzhang
 */

@Component
@Slf4j
public class ApplicationCheck implements InitializationRequired {

    private final Map<String, ApplicationInformationBO> domain2Category = new HashMap<>();
    private final Map<String, ApplicationInformationBO> exactDomain2Category = new HashMap<>();
    private boolean hasExact = false;

    @Autowired
    private AppRuleDao appRuleDao;


    public ApplicationInformationBO getApplicationInformation(Object obj) {
        if (null == obj) {
            return null;
        }
        String str = obj.toString();
        if (this.hasExact && this.exactDomain2Category.containsKey(LevelDomainUtils.ThLD(obj.toString()))) {
            return this.exactDomain2Category.get(LevelDomainUtils.ThLD(obj.toString()));
        }
        if (str.isEmpty() || this.domain2Category.isEmpty()) {
            return null;
        }
        if (str.length() < 4) {
            return null;
        }
        if (str.startsWith("*.")) {
            str = str.substring(2);
        }
        if (str.startsWith("www.")) {
            str = str.substring(4);
        }
        str = str.toLowerCase();
        ApplicationInformationBO appInfo = this.domain2Category.getOrDefault(str, null);
        if (null == appInfo) {
            appInfo = this.domain2Category.getOrDefault(LevelDomainUtils.SLD(str), null);
            if (null == appInfo) {
                appInfo = this.domain2Category.getOrDefault(LevelDomainUtils.TLD(str), null);
            }
        }
        return appInfo;
    }


    private void loadConfiguration(JSONObject json) {
        if (!json.containsKey("title")) {
            return;
        }
        Boolean exact = json.getBoolean("exact");
        ApplicationInformationBO applicationInformation = json.toJavaObject(ApplicationInformationBO.class);
        if (exact!=null && exact) {
            this.exactDomain2Category.put(json.getString("_id").toLowerCase(), applicationInformation);
        } else {
            this.domain2Category.put(json.getString("_id").toLowerCase(), applicationInformation);
        }
    }



    @Override
    public void init() {
        String encFilePath = NodeInfo.getNodeHome() + "/conf/systemRule/apprule.enc";
        File file = new File(encFilePath);
        if (file.exists()) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(encFilePath))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = JSON.parseObject(ToolUtils.decrypt_AES(line, NodeInfo.AES_PASSWORD));
                    loadConfiguration(jsonObject);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            List<JSONObject> allJSON = appRuleDao.findAllJSON();
            allJSON.forEach(this::loadConfiguration);
        }
    }
}

