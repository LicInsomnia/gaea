package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.GaeaData;
import com.tincery.gaea.api.base.SrcRuleDO;
import com.tincery.gaea.api.src.AbstractSrcData;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.rule.AlarmRule;
import org.bson.Document;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author liuming
 */
public class CerAlarmRule extends AlarmRule {

    @Override
    public void init() {
        assetDetector = Config.assetDetector;
        List<Document> defaultAlarmList = Config.cerProperties.getDefaultAlarm();
        List<SrcRuleDO> alarmData = Config.srcRuleDao.getAlarmData(ApplicationInfo.getCategory());
        parseDefaultRule(alarmData, defaultAlarmList);
        alarmData.stream().map(CerAlarmRuleChecker::new).filter(CerAlarmRuleChecker::isActivity).forEach((rule) -> this.ruleCheckers.add(rule));
        if (this.ruleCheckers.isEmpty()) {
            System.out.println("没有加载到cer alarm规则");
        } else {
            this.activity = true;
            System.out.println("共加载了" + this.ruleCheckers.size() +"条cer alarm规则");
        }
    }

    private void parseDefaultRule(List<SrcRuleDO> alarmData, List<Document> defaultAlarmList) {
        for (Document defaultAlarm : defaultAlarmList) {
            SrcRuleDO srcRuleDO = new SrcRuleDO();
            for(String key : defaultAlarm.keySet()) {
                Object value = defaultAlarm.get(key);
                if(value == null) {
                    continue;
                }
                switch(key){
                    case "match_field":
                        srcRuleDO.setMatchField(value.toString());
                        break;
                    case "rule_name" :
                        srcRuleDO.setRuleName(value.toString());
                        break;
                    case "rule_value" :
                        srcRuleDO.setRuleValue(value.toString());
                        break;
                    case "task" :
                        srcRuleDO.setTask(value.toString());
                        break;
                    case "function" :
                        srcRuleDO.setFunction((Integer) value);
                        break;
                    case "type" :
                        srcRuleDO.setType((Integer)value);
                        break;
                    case "category" :
                        srcRuleDO.setCategory(value.toString());
                        break;
                    case "subcategory" :
                        srcRuleDO.setSubcategory(value.toString());
                        break;
                    case "level" :
                        srcRuleDO.setLevel((Integer)value);
                        break;
                    case "range" :
                        srcRuleDO.setRange((Integer)value);
                        break;
                    case "remark" :
                        srcRuleDO.setRemark(value.toString());
                        break;
                    case "mode" :
                        srcRuleDO.setMode((Integer)value);
                        break;
                    case "stattime" :
                        srcRuleDO.setStartTime(date2LocalDateTime((Date)value));
                        break;
                    case "create_user" :
                        srcRuleDO.setCreateUser(value.toString());
                        break;
                    case "isSystem" :
                        srcRuleDO.setIsSystem((Boolean)value);
                        break;
                    case "activity" :
                        srcRuleDO.setActivity((Boolean)value);
                        break;
                        default :
                            break;
                }
            }
            alarmData.add(srcRuleDO);
        }
    }

    private LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    protected class CerAlarmRuleChecker extends AlarmRuleChecker {
        public CerAlarmRuleChecker(SrcRuleDO srcRuleDO) {
            super(srcRuleDO);
        }

        @Override
        public synchronized boolean checkAndStop(AbstractSrcData baseData) {
            CerData data = (CerData) baseData;
            Class<?> clazz = data.getClass();
            // 将所有属性放到set中 最后按配置的规则匹配
            Set<Field> fields = new HashSet<>();
            while (clazz != GaeaData.class) {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }
            String matchValue = null;
            if (this.matchField.contains(".")) {
                // 如果包含 。 说明是级联嵌套内容 需要另外的查询方式
                String[] split = this.matchField.split("\\.");
                String rooFieldName = split[0];
                Optional<Field> rootFieldOptional =
                        fields.stream().filter(field -> field.getName().equals(rooFieldName)).findFirst();
                if (rootFieldOptional.isPresent()) {
                    Field rootField = rootFieldOptional.get();
                    rootField.setAccessible(true);
                    Object rootValue = null;
                    try {
                        rootValue = rootField.get(data);
                        if (rootValue == null) {
                            return false;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    for (int i = 1; i < split.length; i++) {
                        rootValue = getDepthValue(rootValue, split[i]);
                        if (rootValue == null) {
                            return false;
                        }
                    }
                    matchValue = rootValue.toString();
                } else {
                    return false;
                }
            } else {
                // 如果不包含点 直接判断是否存在即可
                Optional<Field> matchFieldOptional =
                        fields.stream().filter(field -> field.getName().equals(this.matchField)).findFirst();
                if (matchFieldOptional.isPresent()) {
                    Field matchField = matchFieldOptional.get();
                    matchField.setAccessible(true);
                    Object value = null;
                    try {
                        value = matchField.get(data);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (value == null) {
                        return false;
                    }
                    matchValue = value.toString();
                }
            }
            if (matchValue == null) {
                return false;
            }
            boolean flag;
            AlarmMaterialData alarmMaterialData = new AlarmMaterialData(data, this.source, null, Config.ipSelector);
            //alarmMaterialData.setPattern(Config.alarmDictionary.valueOf("pattern", "CERT"));
            switch (this.mode) {
                case 0:
                    flag =  matchValue.equals(this.ruleValue.toLowerCase());
                    break;
                case 1:
                    flag =  matchValue.contains(this.ruleValue.toLowerCase());
                    break;
                case 2:
                    flag =  matchValue.startsWith(this.ruleValue.toLowerCase());
                    break;
                case 3:
                    flag = matchValue.endsWith(this.ruleValue.toLowerCase());
                    break;
                case 4:
                    int selfsigned = data.getSelfSigned();
                    flag = (selfsigned == 1 && matchValue.contains(ruleValue));
                    alarmMaterialData.setSha1(data.getId());
                    alarmMaterialData.setCreateUser("system");
                    alarmMaterialData.setIsSystem(true);
                    alarmMaterialData.setType(Config.alarmDictionary.valueOf("type", "cert_analysis"));
                    alarmMaterialData.setCheckMode(5);
                    //alarmMaterialData.setPattern(Config.alarmDictionary.valueOf("pattern", "CERT"));
                    break;
                case 5:
                    int compliance = data.getCompliance();
                    int compliancetype = Integer.parseInt(matchValue);
                    int loc = Integer.parseInt(ruleValue);
                    int weakPubAlgo = (1 << CerComplianceModuleUtils.bWeakPubAlgo);
                    int weakSignAlgo = (1 << CerComplianceModuleUtils.bWeakSignAlgo);
                    int shortKey = (1 << CerComplianceModuleUtils.bShortPubKey);
                    if (compliance == 1 && (compliancetype & loc) == loc) {
                        alarmMaterialData.setCreateUser("system");
                        //alarmMaterialData.setPattern(Config.alarmDictionary.valueOf("pattern", "CERT"));
                        alarmMaterialData.setIsSystem(true);
                        alarmMaterialData.setCheckMode(6);
                        if (loc == weakPubAlgo || loc == weakSignAlgo) {
                            alarmMaterialData.setType(Config.alarmDictionary.valueOf("type", "leak"));
                        } else if (loc == shortKey) {
                            alarmMaterialData.setType(Config.alarmDictionary.valueOf("type", "leak"));
                        } else {
                            alarmMaterialData.setType(Config.alarmDictionary.valueOf("type", "cert_analysis"));
                        }
                        flag = true;
                    } else {
                        flag = false;
                    }
                    break;
                case 6:
                    double reliability = Double.parseDouble(matchValue);
                    String[] valueArray = ruleValue.split(",");
                    double min = "".equals(valueArray[0]) ? Double.NEGATIVE_INFINITY : Double.parseDouble(valueArray[0]);
                    double max = "".equals(valueArray[1]) ? Double.POSITIVE_INFINITY : Double.parseDouble(valueArray[1]);
                    if (reliability <= max && reliability >= min) {
                        alarmMaterialData.setCreateUser("system");
                        //alarmMaterialData.setPattern(Config.alarmDictionary.valueOf("pattern", "CERT"));
                        alarmMaterialData.setIsSystem(true);
                        alarmMaterialData.setType(Config.alarmDictionary.valueOf("type", "cert_analysis"));
                        alarmMaterialData.setCheckMode(7);
                        flag = true;
                    } else {
                        flag = false;
                    }
                    break;
                default:
                    flag = false;
            }
            if(flag) {
                pushAlarm(alarmMaterialData, data.getId());
            }
            return flag;
        }
    }
}