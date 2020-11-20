package com.tincery.dw.commomappdetect.execute;

import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.SearchCondition;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
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
     * 存放当前规则命中的所有时间  在每个文件扫描完成之后通过此集合计算出timeMap
     **/
    Map<AppDetect, List<Long>> hitTimeMap;
    /**
     * 当前步数各个规则命中数量
     */
    Map<AppDetect, Integer> hitCountMap;
    /**
     * 当前步数还需要匹配规则
     **/
    Map<String, Set<AppDetect>> currentAppDetects;
    /**
     * 当前步伐  摩擦摩擦
     */
    volatile int step = 0;
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
            prepareNextStep();
            step++;
        }
        if (this.maybeHitSet.isEmpty()) {
            System.out.println("没有命中的");
        } else {
            System.out.println(this.maybeHitSet.stream()
                    .map(AppDetect::getAppInfo).map(ApplicationInformationBO::getTitle).collect(Collectors.toList()));
        }
        SearchCondition.clearCache();
    }

    private void initFiles(LocalDateTime startTime, LocalDateTime endTime) {
        this.categorySet.forEach(category -> {
            List<String> files = getCsvDataSetBySessionCategory(category, startTime, endTime);
            if (!CollectionUtils.isEmpty(files)) {
                categoryFiles.put(category, files.stream().map(File::new).collect(Collectors.toList()));
            }
        });
    }


    /****
     * 当前步骤匹配结束之后  需要为下一步做一些准备工作
     **/

    private void prepareNextStep() {
        Set<AppDetect> cantHitAppDetect = cantHitAppDetect();
        if (!CollectionUtils.isEmpty(cantHitAppDetect)) {
            System.out.println("移除" + cantHitAppDetect.size()
                    + "个不可能命中的规则" + cantHitAppDetect.stream().map(AppDetect::getId).collect(Collectors.toList()));
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
                have = !value.values().stream().allMatch(Set::isEmpty);
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
        this.hitTimeMap.clear();
    }


    /****
     * 在命中成功之后移除的规则不会影响到此方法
     * 因为这个方法只会针对不可能命中的规则
     * {@link CommonSearchReceiver#successHit}
     **/
    private Set<AppDetect> cantHitAppDetect() {
        Set<AppDetect> shouldRemoveAppDetect = new HashSet<>();
        Map<String, Set<AppDetect>> currentAppDetect = this.stepCategoryMap.get(this.step + 1);
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
        dynamicBox = new ConcurrentHashMap<>();
        userIdMap = new ConcurrentHashMap<>();
        timeMap = new ConcurrentHashMap<>();
        hitCountMap = new ConcurrentHashMap<>();
        hitTimeMap = new ConcurrentHashMap<>();
        categorySet =
                stepCategoryMap.values().stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet());
        categoryFiles = new HashMap<>(categorySet.size());
    }

    /****
     * 上楼  顾名思义 按步匹配
     **/
    private void goUpstairs() {
        // 拿到当前步数的所有规则
        Map<String, Set<AppDetect>> stepMap = stepCategoryMap.get(step + 1);
        CountDownLatch countDownLatch = new CountDownLatch(stepMap.size());
        stepMap.keySet().forEach(category -> executorService.submit(new CategoryProducter(category, countDownLatch)));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /****
     * 判断当前步骤的当前类型是否需要匹配。
     * 当所有规则都命中、或者是确定规则命中不了的时候 就不要匹配了
     **/
    private boolean hitIfNecessary(String category) {
        return !this.currentAppDetects.getOrDefault(category, Collections.emptySet()).isEmpty();
    }

    /****
     * 一个文件执行完之后 需要做一些操作以提高下一个文件的执行效率
     * 计算规则命中次数，如果符合内容，记录标准时间并从当前步骤中移除此规则
     * @param category 当前到底是哪种类型的文件执行完毕  分类型执行的原因是此方法可能为多线程执行，
     *                 同一类型的完成一定是同步的
     **/
    private void prepareNextFile(String category) {
        // 文件执行完之后  计算一次规则命中次数和内容
        Set<AppDetect> appDetects = this.stepCategoryMap.get(this.step + 1).get(category);
        appDetects.forEach(appDetect -> {
            Integer hitCount = hitCountMap.getOrDefault(appDetect, 0);
            int needCount = appDetect.getRules().get(this.step).getCount();
            if (hitCount >= needCount) {
                List<Long> times = hitTimeMap.get(appDetect);
                Collections.sort(times);
                timeMap.put(appDetect, times.get(needCount - 1));
                Set<AppDetect> currentCategoryApp = this.currentAppDetects.get(category);
                currentCategoryApp.remove(appDetect);
            }
        });

    }


    /****
     * 成功命中之后   需要根据命中次数计算时间
     * 如果是第一步的话  保存userId
     * 如果命中次数超过本身需要 而且没有后面的级联操作 将本次内容移除
     **/

    private void successHit(AppDetect appDetect, CsvRow csvRow) {
        hitCountMap.merge(appDetect, 1, Integer::sum);
        List<Long> times = hitTimeMap.computeIfAbsent(appDetect, k -> new ArrayList<>());
        times.add(csvRow.getLong(HeadConst.FIELD.CAPTIME));
        if (this.step == 0) {
            Set<String> userIds = this.userIdMap.computeIfAbsent(appDetect, k -> new HashSet<>());
            String userId = csvRow.get(HeadConst.FIELD.USER_ID);
            userIds.add(userId);
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
        maxStep = stepCategoryMap.size();
        this.currentAppDetects = this.stepCategoryMap.get(1);
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


    private class CategoryProducter implements Runnable {

        final private String category;

        final CountDownLatch countDownLatch;

        private CategoryProducter(String category, CountDownLatch countDownLatch) {
            this.category = category;
            this.countDownLatch = countDownLatch;
        }


        @Override
        public void run() {
            List<File> files = CommonSearchReceiver.this.categoryFiles.get(category);
            for (File file : files) {
                if (hitIfNecessary(category)) {
                    CsvReader csvReader;
                    try {
                        csvReader = CsvReader.builder().file(file).build();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                    int index = 0;
                    CsvRow csvRow;
                    while ((csvRow = csvReader.nextRow()) != null) {
                        CommonAppDetectHitSupport support = new CommonAppDetectHitSupport(mongoTemplate, dynamicBox);
                        for (AppDetect appDetect : CommonSearchReceiver.this.currentAppDetects.get(category)) {
                            if (validUserId(csvRow, appDetect) && validDate(csvRow, appDetect) &&
                                    support.stepHit(csvRow, appDetect, appDetect.getRules().get(step))) {
                                successHit(appDetect, csvRow);
                            }
                        }
                        index++;
                    }
                    prepareNextFile(category);
                    System.out.println("完成" + file.getPath() + "一共" + index + "行");
                }else{
                    System.out.println(category+"完成 不需要继续");
                    break;
                }

            }
            this.countDownLatch.countDown();
        }
    }

}
