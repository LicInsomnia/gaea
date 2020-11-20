package com.tincery.gaea.core.dw;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.KV;
import com.tincery.gaea.api.base.SearchCondition;
import com.tincery.gaea.api.base.SearchRule;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.tincery.gaea.api.dm.FieldCondition.NO_EXIST;
import static com.tincery.gaea.api.dm.FieldCondition.STRING;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 通用挖掘的辅助类
 **/
public class CommonAppDetectHitSupport {


    final private MongoTemplate mongoTemplate;

    final private Map<AppDetect, Map<String, Set<String>>> dynamicBox;

    public CommonAppDetectHitSupport(MongoTemplate mongoTemplate, Map<AppDetect, Map<String, Set<String>>> dynamicBox) {
        this.mongoTemplate = mongoTemplate;
        this.dynamicBox = dynamicBox;
    }

    /****
     * 步匹配
     * @author gxz
     * @param csvRow 此行可能多线程访问 禁止修改
     * @return boolean
     **/
    public boolean stepHit(final CsvRow csvRow, AppDetect appDetect, SearchRule searchRule) {
        boolean hit = searchRule.getMatch().getValue().stream()
                .allMatch(definition -> definitionHit(csvRow, definition, appDetect));
        // 如果需要记录out 记录一下out
        List<String> outs = searchRule.getOut();
        if (hit && !CollectionUtils.isEmpty(outs)) {
            recordOuts(outs, csvRow, appDetect);
        }
        return hit;
    }

    private void recordOuts(List<String> outs, final CsvRow csvRow, AppDetect appDetect) {
        Map<String, Set<String>> dynamics = this.dynamicBox.computeIfAbsent(appDetect, k -> new HashMap<>());
        for (String out : outs) {
            Set<String> strings = dynamics.computeIfAbsent(out, k -> new HashSet<>());
            String outValue = csvRow.get(out);
            if (outValue != null) {
                strings.add(outValue);
            }
        }
    }

    /****
     * 步匹配中分成多个行   此方法是行匹配
     * @return boolean
     **/
    private boolean definitionHit(final CsvRow csvRow, String definition, AppDetect appDetect) {
        List<SearchCondition> conditions = appDetect.getConditions();
        boolean result = true;
        String[] indexs;
        if (definition.contains(",")) {
            indexs = definition.split(",");
        } else {
            indexs = new String[]{definition};
        }
        for (String index : indexs) {
            int i = Integer.parseInt(index);
            if (i > 0) {
                result &= hit(csvRow, conditions.get(i - 1), appDetect);
            } else {
                result |= hit(csvRow, conditions.get(-(i - 1)), appDetect);
            }
        }
        return result;
    }

    /***
     * 一行可能有多个规则   此方法是规则匹配
     * @return boolean
     **/
    private boolean hit(final CsvRow csvRow, SearchCondition searchCondition, AppDetect appDetect) {
        Map<String, Set<String>> dynamics = dynamicBox.get(appDetect);
        // dynamics即使是null也不要紧 如果是null不会进入到那一行方法
        return searchCondition.hit(csvRow, mongoTemplate, dynamics);
    }


    public static Map<Integer, Map<String, Set<AppDetect>>> stepCategoryGroup(Set<AppDetect> allAppDetect) {
        return allAppDetect.stream().collect(new GroupCollectors());
    }


    public static class GroupCollectors implements Collector<AppDetect, Map<Integer, Map<String, Set<AppDetect>>>,
            Map<Integer, Map<String, Set<AppDetect>>>> {

        @Override
        public Supplier<Map<Integer, Map<String, Set<AppDetect>>>> supplier() {
            return ConcurrentHashMap::new;
        }

        @Override
        public BiConsumer<Map<Integer, Map<String, Set<AppDetect>>>, AppDetect> accumulator() {
            return (map, app) -> {
                int appStep = app.getRuleCount();
                for (int i = 0; i < appStep; i++) {
                    SearchRule searchRule = app.getRules().get(i);
                    String category = searchRule.getMatch().getKey();
                    Map<String, Set<AppDetect>> categoryMap = map.computeIfAbsent(i + 1, k -> new ConcurrentHashMap<>());
                    Set<AppDetect> appDetects = categoryMap.computeIfAbsent(category, k -> new ConcurrentHashSet<>());
                    appDetects.add(app);
                }
            };
        }

        @Override
        public BinaryOperator<Map<Integer, Map<String, Set<AppDetect>>>> combiner() {
            return (map1, map2) -> {
                map1.putAll(map2);
                return map1;
            };
        }

        @Override
        public Function<Map<Integer, Map<String, Set<AppDetect>>>, Map<Integer, Map<String, Set<AppDetect>>>> finisher() {
            return (x) -> x;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }

    }

    public static void main(String[] args) {
        AppDetect appDetect = new AppDetect();
        AppDetect.AlertInfo info = new AppDetect.AlertInfo();
        info.setCategory("dynamic").setSubcategoryDesc("TorVpn")
                .setSubcategory("special_app").setCategoryDesc("VPN翻墙").setLevel(3).setAccuracy("疑似").setRemark("准确率90%，一步，ssl");
        appDetect.setId("TorVpn_forwin").setAlertInfo(info);
        ApplicationInformationBO applicationInformationBO = new ApplicationInformationBO();
        applicationInformationBO.setType(Arrays.asList("重点关注@重点关注","日常生活@VPN翻墙")).setSpecialTag(Arrays.asList("VPN翻墙"))
                .setTitle("TorVpn_forwin");
        appDetect.setAppInfo(applicationInformationBO);
        List<SearchCondition> list = new ArrayList<>();
        list.add((SearchCondition)new SearchCondition().setOrder(1).setField("servername").setValue("ajax.aspnetcdn.com").setType(4).setOperator(1));
        list.add((SearchCondition)new SearchCondition().setOrder(2).setField("serverCerchain").setType(STRING).setOperator(NO_EXIST));
        list.add((SearchCondition)new SearchCondition().setOrder(3).setField("clientCerchain").setType(STRING).setOperator(NO_EXIST));
        appDetect.setConditions(list);
        List<SearchRule> ruleList = new ArrayList<>();
        KV<String,List<String>> kv = new KV<>("ssl",Arrays.asList("1","2","3"));
        ruleList.add(new SearchRule().setMatch(kv).setCount(1));
        appDetect.setRules(ruleList);

    }
}
