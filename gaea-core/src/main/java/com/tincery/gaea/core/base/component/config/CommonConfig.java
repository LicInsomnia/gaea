package com.tincery.gaea.core.base.component.config;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.exception.InitException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/

@ConfigurationProperties(prefix = "common")
@Component
@Data
@Slf4j
public class CommonConfig {
    private Authentication authentication;
    private EmailConfig email;
    private List<String> cerKeys;
    private Location dflocation;


    @Setter@Getter
    public static class Authentication{
        private Date authorizationDate;
        private boolean perpetualLicense;
        private List<String> processorIdList;
    }
    @Setter@Getter
    public static class EmailConfig{
        private AttchAlertInfo attchAlertinfo;
        private boolean perpetualLicense;
        private List<String> whiteSuffix;
    }

    @Setter@Getter
    public static class AttchAlertInfo{
        private String categoryDesc;
        private String subcategoryDesc;
        private Integer level;
    }











    private static final Map<String, Object> COMMON_CONFIG = new HashMap<>();

    public static void put(String key, Object value) {
        COMMON_CONFIG.put(key, value);
    }

    public static Object get(String key) {
        return COMMON_CONFIG.get(key);
    }

    /*****
     * 把CommonConfig 和RunConfig 合并内容
     * 在已经加载完commonConfig后使用
     * @author gxz
     * @date 2020/8/15
     **/
    public static void mergeCommonRun(String runKey, Object runValue) {
        if (COMMON_CONFIG.containsKey(runKey)) {
            try {
                JSONObject commonValue = new JSONObject((Map) COMMON_CONFIG.get(runKey));
                commonValue.putAll(new JSONObject((Map) runValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            COMMON_CONFIG.put(runKey, runValue);
        }
    }

    /****
     * 在完成初始化之后调用此方法 执行校验
     * @author gxz
     * @date 2020/8/15
     **/
    public static void validatorCommonConfig() {
        if (COMMON_CONFIG.isEmpty()) {
            String message = "commonConfig 没有内容 初始化失败";
            log.error(message);
            throw new InitException(message);
        }
        log.info("初始化commonConfig成功");
    }
}
