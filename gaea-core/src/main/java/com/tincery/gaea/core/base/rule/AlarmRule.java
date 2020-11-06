package com.tincery.gaea.core.base.rule;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.dao.SrcRuleDao;
import com.tincery.gaea.core.base.tool.util.FileWriter;
import com.tincery.gaea.core.base.tool.util.ObjectUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Insomnia
 * 1.0.1:新增initialize 函数重载 20190102
 * @version 1.0.3
 */
@Component
@Slf4j
public class AlarmRule extends BaseSimpleRule {

    public static final Map<String, AlarmMaterialData> alarmList = new HashMap<>();
    public static final List<String> eventDataList = new ArrayList<>();
    /**
     * IP地址库
     */
    @Autowired
    protected IpSelector ipSelector;
    /**
     * 资产检测模块
     */
    @Autowired
    protected AssetDetector assetDetector;
    /**
     * 告警信息标签
     */
    protected String category;
    @Autowired
    private SrcRuleDao srcRuleDao;

    public static void writeAlarm(String path, String prefix, int maxLine) {
        if (alarmList.size() == 0) {
            log.info("未检测到任何告警信息");
            return;
        }
        String file = path + prefix + "alarm_" + System.currentTimeMillis() + ".json";
        try (FileWriter alarmWriter = new FileWriter()) {
            alarmWriter.set(file);
            int count = 0;
            for (AlarmMaterialData alarmMaterialData : alarmList.values()) {
                alarmWriter.write(JSONObject.toJSONString(alarmMaterialData));
                if (count++ >= maxLine) {
                    alarmWriter.close();
                    file = path + "alarm_" + System.currentTimeMillis() + ".json";
                    alarmWriter.set(file);
                    count = 0;
                }
            }
            log.info("Out put " + alarmList.size() + " alarm materials...");
            alarmList.clear();
        }
    }

    public static void writeEvent(String path, String prefix, int maxLine) {
        if (eventDataList.isEmpty()) {
            return;
        }
        String file = path + prefix + "alarm_" + System.currentTimeMillis() + ".json";
        try (FileWriter eventWriter = new FileWriter()) {
            eventWriter.set(file);
            int count = 0;
            for (String eventData : eventDataList) {
                eventWriter.write(eventData);
                count++;
                if (count > maxLine) {
                    eventWriter.set(file);
                    count = 0;
                }
            }
            log.info("Out put " + eventDataList.size() + " ...");
            eventDataList.clear();
        }
    }

    public int alarmCount() {
        return alarmList.size();
    }

    protected void pushAlarm(AlarmMaterialData alarmMaterialData, String keyStr) {
        alarmMaterialData.setKey(keyStr);
        alarmMaterialData.setAsset(this.assetDetector);
        //根据自定义键值获取该条告警信息key
        String md5Key = alarmMaterialData.getKey();
        if (null == md5Key) {
            return;
        }
        if (alarmList.containsKey(md5Key)) {
            //若已经存在该条告警
            alarmMaterialData.merge(alarmList.get(md5Key));
            alarmList.replace(md5Key, alarmMaterialData);
        } else {
            alarmList.put(md5Key, alarmMaterialData);
            eventDataList.add(alarmMaterialData.getEventData());
        }
    }

    @Override
    public void init() {
        List<SrcRuleDO> alarmData = srcRuleDao.getAlarmData(ApplicationInfo.getCategory());
        alarmData.stream().map(AlarmRuleChecker::new).filter(AlarmRuleChecker::isActivity).forEach((rule) -> this.ruleCheckers.add(rule));
        if (this.ruleCheckers.isEmpty()) {
            log.warn("没有加载到alarm规则");
        } else {
            this.activity = true;
            log.info("共加载了{}条alarm规则", this.ruleCheckers.size());
        }

    }


    private class AlarmRuleChecker extends AbstractRuleChecker {

        private final SrcRuleDO source;
        private boolean activity = true;


        /****
         * 你并不能阻止构造方法创建对象 所以我们在此处判断一下  如果不符合标准 就将activity置为false 在上层过滤掉
         * @see AlarmRule #init()
         **/
        public AlarmRuleChecker(SrcRuleDO srcRuleDO) {
            super(srcRuleDO);
            this.source = srcRuleDO;
            if (!effective(srcRuleDO)) {
                log.warn("src_rule表中id为{}的数据  在解析为alarm规则的时候解析失败", srcRuleDO.getId());
                this.activity = false;
            }
        }

        public boolean isActivity() {
            return this.activity;
        }

        /****
         * srcRule 映射成rule之前 先判断是否有效
         **/
        public boolean effective(SrcRuleDO rule) {
            return ObjectUtils.allNotNull(rule.getRuleName(),
                    rule.getCreateUser(),
                    StringUtils.isEmpty(rule.getMatchField()),
                    StringUtils.isEmpty(ruleValue),
                    rule.getMode(), rule.getCategory(), rule.getSubcategory());
        }


        /****
         * 告警规则匹配
         * 匹配到告警规则  需要把告警素材添加进集合 等待处理
         * 同时将eventData 添加到map中
         * @author gxz
         **/
        @Override
        public synchronized boolean checkAndStop(AbstractSrcData data) {
            if (super.checkAndStop(data)) {
                String context = null;
                // 0代表http 需要特殊处理一下
                if (this.mode.equals(0)) {
                    Class<? extends AbstractSrcData> clazz = data.getClass();
                    try {
                        Field declaredField = clazz.getDeclaredField(this.matchField);
                        String matchValue = declaredField.get(data).toString().toLowerCase();
                        int index = matchValue.indexOf(this.ruleValue);
                        int min = Math.max(0, index - 20);
                        int max = Math.min(index + 20, matchValue.length());
                        context = matchValue.substring(min, max);
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    }
                }
                AlarmMaterialData alarmMaterialData = new AlarmMaterialData(data, this.source, context, AlarmRule.this.ipSelector);
                /*alarmRule 是 src告警*/
                alarmMaterialData.setPattern(1);
                pushAlarm(alarmMaterialData, null);
            }
            return false;
        }
    }
}
