package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class FieldCondition {

    protected static final int EQUALS = 1;
    protected static final int NO_EQUALS = 2;
    protected static final int CONTAIN = 3;
    protected static final int NO_CONTAIN = 4;
    protected static final int GT = 5;
    protected static final int GTE = 6;
    protected static final int LT = 7;
    protected static final int LTE = 8;
    protected static final int AFTER = 9;
    protected static final int BEFORE = 10;
    protected static final int TRUE = 11;
    protected static final int FALSE = 12;
    protected static final int EXIST = 13;
    protected static final int NO_EXIST = 14;
    protected static final int START_WITH = 15;
    protected static final int END_WITH = 16;
    protected static final int IN = 17;


    protected static final int INT = 1;
    protected static final int LONG = 2;
    protected static final int DOUBLE = 3;
    protected static final int STRING = 4;
    protected static final int DATE = 5;
    protected static final int BOOLEAN = 6;
    protected static final int ARRAY = 7;

    protected String field;
    protected Object value;
    protected int operator;
    protected int type;

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
                return Objects.equals(value,targetValue);
            case NO_EQUALS:
                return !Objects.equals(value,targetValue);
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
            case START_WITH:
                return value.startsWith(targetValue);
            case END_WITH:
                return value.endsWith(targetValue);
            default:
                throw new IllegalArgumentException("String operator can't is " + operator);
        }
    }
}
