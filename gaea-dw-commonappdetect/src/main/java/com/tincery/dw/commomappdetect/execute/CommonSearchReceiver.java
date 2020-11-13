package com.tincery.dw.commomappdetect.execute;

import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.core.base.dao.AppDetectDao;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvReader;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import com.tincery.gaea.core.dw.AbstractDataWarehouseReceiver;
import com.tincery.gaea.core.dw.CommonAppDetectHitSupport;
import com.tincery.gaea.core.dw.DwProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
public class CommonSearchReceiver extends AbstractDataWarehouseReceiver {


    @Autowired
    private AppDetectDao appDetectDao;


    /**
     * 所有的规则(list)
     */
    List<AppDetect> allDetects;

    /**
     * 所有的规则(set 为了增加移除效率)
     */
    Set<AppDetect> maybeHitSet;
    /**
     * 根据步骤和category分组
     */
    Map<Integer, Map<String, Set<AppDetect>>> stepCategoryMap;
    /**
     * 存放动态内容的集合 每个AppDetect都拥有一个Map
     */
    Map<AppDetect, Map<String, Set<String>>> dynamicBox;
    /**
     * 存放各个规则的userId
     */
    Map<AppDetect, Set<String>> userIdMap;
    /**
     * 存放各个规则最早执行时间 这是通过计算的出的
     */
    Map<AppDetect, Long> timeMap;
    /**
     * 当前步数各个规则命中数量
     */
    Map<AppDetect, Integer> hitCountMap;
    /**
     * 当前步伐  摩擦摩擦
     */
    volatile int step = 0;
    /**
     * 当前正在循环的类型
     */
    volatile String category;
    /**
     * 一共得走多少步
     **/
    int maxStep;
    /**
     * 把当前时段的文件扫描出来 保存起来  免除重复打开句柄
     */
    Map<String, List<File>> categoryFiles;
    /**
     *
     */
    Set<String> categorySet;


    @Override
    public synchronized void dataWarehouseAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        initFiles(startTime, endTime);
        // 根据步长进行匹配 一次循环匹配一步
        while (step < maxStep) {
            goUpstairs();
            prepareNext();
            step++;
        }
        if (this.maybeHitSet.isEmpty()) {
            System.out.println("没有命中的");
        } else {
            System.out.println(this.maybeHitSet);
        }
    }

    private void initFiles(LocalDateTime startTime, LocalDateTime endTime) {
            this.categorySet.forEach(category->{
                List<String> files = getCsvDataSetBySessionCategory(category, startTime, endTime);
                if(!CollectionUtils.isEmpty(files)){
                    categoryFiles.put(category,files.stream().map(File::new).collect(Collectors.toList()));
                }
            });
    }


    /****
     * 当前步骤匹配结束之后  需要为下一步做一些准备工作
     **/

    private void prepareNext() {
        Set<AppDetect> cantHitAppDetect = cantHitAppDetect();
        if (!CollectionUtils.isEmpty(cantHitAppDetect)) {
            this.maybeHitSet.removeAll(cantHitAppDetect);
            this.stepCategoryMap.forEach((step, categoryMap) ->
                    categoryMap.forEach((category, appDetectSet) ->
                            appDetectSet.removeAll(cantHitAppDetect)));
        }
        // 移除之后 判断一下之后还有没有了
        this.stepCategoryMap.remove(this.step + 1);
        boolean have = false;
        if (!this.stepCategoryMap.isEmpty()) {
            for (Map<String, Set<AppDetect>> value : this.stepCategoryMap.values()) {
                have = !value.values().isEmpty();
                if (have) {
                    break;
                }
            }
        }
        if (!have) {
            this.step = this.maxStep;
            return;
        }
        this.hitCountMap.clear();
    }

    private Set<AppDetect> cantHitAppDetect() {
        Set<AppDetect> shouldRemoveAppDetect = new HashSet<>();
        Map<String, Set<AppDetect>> currentAppDetect = this.stepCategoryMap.get(this.step);
        currentAppDetect.values().forEach(appDetects -> appDetects.forEach(appDetect -> {
            int shouldHitCount = appDetect.getRules().get(this.step).getCount();
            int hitCount = hitCountMap.getOrDefault(appDetect, 0);
            // 如果命中的次数没有达到要求 说明此条没命中 之后的规则也不需要匹配了
            if (hitCount < shouldHitCount) {
                shouldRemoveAppDetect.add(appDetect);
            }
        }));
        return shouldRemoveAppDetect;
    }


    private void initAndGroupAppDetects() {
        maybeHitSet = new HashSet<>(allDetects);
        stepCategoryMap = CommonAppDetectHitSupport.stepCategoryGroup(maybeHitSet);
        dynamicBox = new HashMap<>();
        userIdMap = new HashMap<>();
        timeMap = new HashMap<>();
        categorySet = stepCategoryMap.values().stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /****
     * 上楼  顾名思义 按步匹配
     **/
    private void goUpstairs() {
        // 拿到当前步数的所有规则
        stepCategoryMap.get(step + 1).forEach((category, rules) -> {
            List<File> files = this.categoryFiles.get(category);
            files.forEach(file -> {
                CsvReader csvReader = null;
                try {
                    csvReader = CsvReader.builder().file(file).build();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                }
                CsvRow csvRow;
                while ((csvRow = csvReader.nextRow()) != null) {
                    CommonAppDetectHitSupport support = new CommonAppDetectHitSupport(mongoTemplate, dynamicBox);
                    for (AppDetect appDetect : rules) {
                        if (validUserId(csvRow, appDetect) && validDate(csvRow, appDetect) &&
                                support.stepHit(csvRow, appDetect, appDetect.getRules().get(step))) {
                            successHit(appDetect, csvRow);
                        }
                    }
                }
            });
        });
    }


    /****
     * 成功命中之后   需要根据命中次数计算时间
     * 如果是第一步的话  保存userId
     **/

    private void successHit(AppDetect appDetect, CsvRow csvRow) {
        int hitCount = hitCountMap.merge(appDetect, 1, Integer::sum);

        if (this.step == 0) {
            Set<String> userIds = this.userIdMap.computeIfAbsent(appDetect, k -> new HashSet<>());
            String userId = csvRow.get(HeadConst.FIELD.USER_ID);
            userIds.add(userId);
        }
        if (hitCount == appDetect.getRules().get(this.step).getCount()) {
            timeMap.put(appDetect, csvRow.getLong(HeadConst.FIELD.CAPTIME));
        }

    }


    private boolean validDate(CsvRow csvRow, AppDetect appDetect) {
        // 第一步不需要判断 因为还计算不出时间
        if (step == 0) {
            return true;
        }
        return this.timeMap.getOrDefault(appDetect, 0L) < csvRow.getLong(HeadConst.FIELD.CAPTIME);
    }

    private boolean validUserId(CsvRow csvRow, AppDetect appDetect) {
        // 第一步不需要判断 因为还不知道userId内容
        if (this.step == 0) {
            return true;
        }
        Set<String> userIds = userIdMap.get(appDetect);
        return userIds.contains(csvRow.get(HeadConst.FIELD.USER_ID));
    }

    @Override
    public void init() {
        // 拿到所有的App规则  转成set 便于将来移除
        allDetects = appDetectDao.findAll();
        initAndGroupAppDetects();
        this.step = this.stepCategoryMap.size();
    }

    @Override
    @Autowired
    public void setProperties(DwProperties dwProperties) {
        this.dwProperties = dwProperties;
    }

    @Override
    @Resource(name = "sysMongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


}
