package com.tincery.gaea.api.base;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.FieldCondition;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SearchCondition extends FieldCondition {

    public static Map<String, Set<String>> mongoCache = new ConcurrentHashMap<>();

    private int order;

    private FieldCondition cerRule;

    public static void clearCache(){
        mongoCache.clear();
    }

    public boolean hit(final CsvRow csvRow, MongoTemplate mongoTemplate, Map<String, Set<String>> dynamicBox) {
        if (isMongo()) {
            return mongoHit(csvRow, mongoTemplate);
        }

        if (isDynamic()) {
            return dynamicHit(csvRow, dynamicBox);
        }

        return super.hit(csvRow.toJsonObject());

    }

    private boolean isMongo() {
        return this.value instanceof String && this.value.toString().startsWith("#mongo");
    }

    private boolean isDynamic() {
        return this.value instanceof String && this.value.toString().startsWith("#dynamic");
    }

    private boolean mongoHit(final CsvRow csvRow, MongoTemplate mongoTemplate) {
        Set<String> values = mongoCache.computeIfAbsent(this.value.toString(), (k) -> getMongoCache(k, mongoTemplate));
        return values.contains(csvRow.get(this.field));
    }

    private Set<String> getMongoCache(String mongoFindStr, MongoTemplate mongoTemplate) {
        String[] split = mongoFindStr.split("#");
        String collectionName = split[2];
        String fieldName = split[3];
        Query query = new Query();
        query.addCriteria(Criteria.where(fieldName).is(value));
        Field fields = query.fields();
        fields.include("_id");
        List<JSONObject> jsonObjects = mongoTemplate.find(query, JSONObject.class, collectionName);
        return CollectionUtils.isEmpty(jsonObjects) ?
                Collections.emptySet() :
                jsonObjects.stream().map(jsonObject -> jsonObject.getString("_id")).collect(Collectors.toSet());
    }

    private boolean dynamicHit(final CsvRow csvRow, Map<String, Set<String>> dynamicBox) {
        String value = csvRow.get(this.field);
        String dynamicName = this.value.toString().substring("#dynamic".length());
        Set<String> dynamics = dynamicBox.get(dynamicName);
        if (dynamics == null) {
            return false;
        }
        return dynamics.contains(value);
    }

}
