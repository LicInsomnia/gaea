package com.tincery.gaea.core.base.component.support;


import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.core.base.exception.InitException;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.starter.base.InitializationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载sys.application_protocol生成会话协议应用检测器
 *
 * @author gongxuanzhang
 */

@Slf4j
@Component
public class ApplicationProtocol implements InitializationRequired {

    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    private Map<String, ApplicationInformationBO> key2App = new HashMap<>();

    private Map<String, Boolean> app2IsEnc = new HashMap<>();

    public final Map<String, ApplicationInformationBO> getKey2App() {
        return this.key2App;
    }

    public final Boolean isEnc(String key) {
        return this.app2IsEnc.getOrDefault(key, null);
    }

    public ApplicationInformationBO getApplication(String key) {
        return this.key2App.getOrDefault(key, null);
    }

    public String getProNameOrDefault(String key, String defaultProName){
        ApplicationInformationBO orDefault = this.key2App.getOrDefault(key, null);
        if(orDefault == null){
            return defaultProName;
        }else{
            return orDefault.getProName();
        }
    }

    public String getProName(String key){
        if(this.key2App.containsKey(key)){
            return this.key2App.get(key).getProName();
        }
        return null;
    }

    @Override
    public void init() {
        log.info("开始加载application protocol ...");
        List<ApplicationInformationBO> all = sysMongoTemplate.findAll(ApplicationInformationBO.class,"application_protocol");
        for (ApplicationInformationBO applicationInformationBO : all) {
            this.key2App.put(applicationInformationBO.getId(), applicationInformationBO);
            String proName = applicationInformationBO.getTitle();
            if (StringUtils.isNotEmpty(proName)) {
                this.app2IsEnc.put(proName, applicationInformationBO.getEnc());
            }
        }
        if (key2App.isEmpty()) {
            log.error("加载application protocol失败");
            throw new InitException();
        }
        log.info("加载application protocol完成,共加载了{}个protocol", key2App.size());
    }

}
