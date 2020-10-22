package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class AssetCondition extends SimpleBaseDO {

    private static final int EQUALS = 1;
    private static final int NO_EQUALS = 2;
    private static final int CONTAIN = 3;
    private static final int NO_CONTAIN = 4;
    private static final int GT = 5;
    private static final int GTE = 6;
    private static final int LT = 7;
    private static final int LTE = 8;
    private static final int AFTER = 9;
    private static final int BEFORE = 10;
    private static final int TRUE = 11;
    private static final int FALSE = 12;
    private static final int EXIST = 13;
    private static final int NO_EXIST = 14;

    private static final int INT = 1;
    private static final int LONG = 2;
    private static final int DOUBLE = 3;
    private static final int STRING = 4;
    private static final int DATE = 5;
    private static final int BOOLEAN = 6;
    private static final int ARRAY = 7;


    @Id
    private String id;

    private boolean activity;

    private String proname;

    private String description;

    private List<List<Condition>> conditionGroup;

    /***是否为黑名单  false就是白名单*/
    private boolean blackList;


    @Setter
    @Getter
    public static class Condition {
        private String field;
        private Object value;
        private int operator;
        private int type;

        public boolean hit(JSONObject jsonObject) {
            if (!jsonObject.containsKey(field)) {
                return this.type == NO_EXIST;
            }
            if (this.type == EXIST) {
                return jsonObject.containsKey(field);
            }
            Object value = findValue(jsonObject);
            switch (this.type) {
                case INT:
                    return matchInteger(Integer.parseInt(value.toString()));
                case LONG:
                    return matchLong(Long.parseLong(value.toString()));
                case DOUBLE:
                    return matchDouble(Double.parseDouble(value.toString()));
                case STRING:
                    return matchString(value.toString());
                case DATE:
                    return matchDate(value.toString());
                case BOOLEAN:
                    return matchBoolean(Boolean.getBoolean(value.toString()));
                case ARRAY:
                    return matchArray((JSONArray) value);
                default:
                    throw new IllegalArgumentException("type can't is " + this.type);
            }
        }

        private Object findValue(JSONObject jsonObject) {
            if (this.field.contains(".")) {
                String[] split = field.split(".");
                JSONObject box = jsonObject.getJSONObject(split[0]);
                for (int i = 1; i < split.length - 1; i++) {
                    box = box.getJSONObject(split[i]);
                }
                return box.get(split[split.length - 1]);
            } else {
                return jsonObject.get(this.field);
            }
        }

        private boolean matchInteger(int value) {
            int targetValue = Integer.parseInt(this.value.toString());
            switch (operator) {
                case EQUALS:
                    return value == targetValue;
                case NO_EQUALS:
                    return value != targetValue;
                case GT:
                    return value > targetValue;
                case GTE:
                    return value >= targetValue;
                case LT:
                    return value < targetValue;
                case LTE:
                    return value <= targetValue;
                default:
                    throw new IllegalArgumentException("number operator can't is " + operator);
            }
        }

        private boolean matchLong(Long value) {
            long targetValue = Long.parseLong(this.value.toString());
            switch (operator) {
                case EQUALS:
                    return value == targetValue;
                case NO_EQUALS:
                    return value != targetValue;
                case GT:
                    return value > targetValue;
                case GTE:
                    return value >= targetValue;
                case LT:
                    return value < targetValue;
                case LTE:
                    return value <= targetValue;
                default:
                    throw new IllegalArgumentException("number operator can't is " + operator);
            }
        }

        private boolean matchDouble(double value) {
            double targetValue = Double.parseDouble(this.value.toString());
            switch (operator) {
                case EQUALS:
                    return value == targetValue;
                case NO_EQUALS:
                    return value != targetValue;
                case GT:
                    return value > targetValue;
                case GTE:
                    return value >= targetValue;
                case LT:
                    return value < targetValue;
                case LTE:
                    return value <= targetValue;
                default:
                    throw new IllegalArgumentException("number operator can't is " + operator);
            }
        }

        private boolean matchDate(String dateString) {
            LocalDateTime value = LocalDateTime.parse(dateString, DateUtils.DEFAULT_DATE_PATTERN);
            LocalDateTime targetValue = LocalDateTime.parse(this.value.toString(), DateUtils.DEFAULT_DATE_PATTERN);
            switch (operator) {
                case AFTER:
                    return value.isAfter(targetValue);
                case BEFORE:
                    return value.isBefore(targetValue);
                default:
                    throw new IllegalArgumentException("date operator can't is " + operator);
            }
        }

        private boolean matchBoolean(boolean value) {
            switch (operator) {
                case TRUE:
                    return value;
                case FALSE:
                    return !value;
                default:
                    throw new IllegalArgumentException("boolean operator can't is " + operator);
            }
        }

        private boolean matchArray(JSONArray jsonArray) {
            switch (operator) {
                case CONTAIN:
                    return jsonArray.contains(this.value);
                case NO_CONTAIN:
                    return !jsonArray.contains(this.value);
                default:
                    throw new IllegalArgumentException("array operator can't is " + operator);
            }
        }

        private boolean matchString(String value) {
            String targetValue = this.value.toString();
            switch (operator) {
                case EQUALS:
                    return value.equals(targetValue);
                case NO_EQUALS:
                    return !value.equals(targetValue);
                case CONTAIN:
                    return value.contains(targetValue);
                case NO_CONTAIN:
                    return !value.contains(targetValue);
                default:
                    throw new IllegalArgumentException("String operator can't is " + operator);
            }
        }
    }

    /****
     * 是否命中
     * @author gxz
     * @param jsonObject 一条json数据
     **/
    public boolean hit(JSONObject jsonObject) {
      /*  for (Condition conditionItem : this.conditionGroup) {
            if (!conditionItem.hit(jsonObject)) {
                return false;
            }
        }*/
        return true;
    }


}
