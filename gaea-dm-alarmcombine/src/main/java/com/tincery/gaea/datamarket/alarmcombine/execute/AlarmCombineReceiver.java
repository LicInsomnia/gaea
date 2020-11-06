package com.tincery.gaea.datamarket.alarmcombine.execute;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.dm.Alarm;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.dao.AlarmDao;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.datamarket.alarmcombine.mgt.AlarmDictionary;
import com.tincery.gaea.datamarket.alarmcombine.property.AlarmCombineProperties;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sparrow
 */
@Slf4j
@Service
public class AlarmCombineReceiver implements Receiver {


    private AlarmCombineProperties properties;
    @Autowired
    private AlarmDictionary alarmDictionary;
    @Autowired
    private AlarmDao alarmDao;

    @Autowired
    public void setProperties(AlarmCombineProperties properties) {
        this.properties = properties;
    }

    /**
     * @param textMessage 接收到的消息  存放要扫描的目录名
     * @throws JMSException
     */
    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File fileFolder = new File(textMessage.getText());
//        String accuracy = alarmDictionary.parse("accuracy", 1);

        if (!fileFolder.isDirectory()){
            throw new JMSException("消息错误，请输入有效文件夹");
        }

        ConcurrentHashMap<String, Pair<Alarm,Integer>> alarmMap = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<Alarm> resultList = new CopyOnWriteArrayList<>();
        File [] fileList = fileFolder.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileList[i]))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    AlarmMaterialData alarmMaterialData = JSON.parseObject(line).toJavaObject(AlarmMaterialData.class);
                    //TODO 这里需要区分到底是什么类型的告警 然后准备合并
                    Alarm newAlarm = new Alarm(alarmMaterialData);
                    adjustAlarm(newAlarm,alarmMaterialData);
                    Integer pattern = alarmMaterialData.getPattern();
                    if (pattern == 0){
                        //证书告警不合并
                        resultList.add(newAlarm);
                    }
                    String key = getAlarmKeyByPattern(pattern, alarmMaterialData);
                    if (StringUtils.isEmpty(key)){
                        continue;
                    }
                    Pair<Alarm, Integer> kv = alarmMap.getOrDefault(key, null);
                    if (Objects.isNull(kv)){
                        alarmMap.put(key,new Pair<>(newAlarm,1));
                    }else{
                        Alarm oldAlarm = kv.getKey();
                        //合并告警
                        Alarm mergeData = mergeAlarm(pattern,newAlarm,oldAlarm);
                        if (Objects.isNull(mergeData)){
                            //如果返回的null  则不合并 分别存储
                            resultList.add(adjustDescription(oldAlarm,kv.getValue()));
                            alarmMap.put(key,new Pair<>(newAlarm,1));
                        }else{
                            //合并 存储在告警map中 最后输出
                            alarmMap.put(key,new Pair<>(mergeData,kv.getValue()+1));
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //输出告警Alarm
        free(resultList,alarmMap);
    }

    /**
     * 输出告警信息
     * @param resultList 要输出的集合
     * @param alarmMap 还未填装属性的集合
     */
    private void free(CopyOnWriteArrayList<Alarm> resultList, ConcurrentHashMap<String, Pair<Alarm, Integer>> alarmMap) {
        alarmMap.forEach((key,value)->{
            resultList.add(adjustDescription(value.getKey(),value.getValue()));
        });
        alarmDao.insert(resultList);
    }

    /**
     * 封装元数据的码表信息  进入告警
     * 将元数据的eventData 填入告警
     * @param alarm  告警实体
     * @param alarmMaterialData 元数据
     */
    private void adjustAlarm(Alarm alarm,AlarmMaterialData alarmMaterialData){
        String category = alarmDictionary.parse("category", alarmMaterialData.getCategory());
        alarm.setCategory(category);
        String level = alarmDictionary.parse("level", alarmMaterialData.getLevel());
        alarm.setLevel(level);
        String checkMode = alarmDictionary.parse("checkmode", alarmMaterialData.getCheckMode());
        alarm.setCheckMode(checkMode);
//        alarmDictionary.parse("function",alarmMaterialData.getFunction) 元数据没有这个字段
        String accuracy = alarmDictionary.parse("accuracy", alarmMaterialData.getAccuracy());
        alarm.setAccuracy(accuracy);
//        alarmDictionary.parse("range",alarmMaterialData.getRange) 元数据没有这个字段
        String type = alarmDictionary.parse("type", alarmMaterialData.getType());
        alarm.setType(type);
        String pattern = alarmDictionary.parse("pattern", alarmMaterialData.getPattern());
        alarm.setPattern(pattern);
        ArrayList<String> eventData = new ArrayList<>();
        eventData.add(alarmMaterialData.getEventData());
        alarm.setEventData(eventData);
    }

    /**
     * 填充orDefault的 description
     * @param oldAlarm
     * @param times 命中次数
     * @return alarmMaterialData
     */
    private Alarm adjustDescription(Alarm oldAlarm, Integer times) {
        String pattern = oldAlarm.getPattern();
        switch (pattern){
            case "SRC":
                //src装填description
                adjustSrcDescription(oldAlarm,times);
                break;
            case "DW":
                //dw 装填description
                adjustDWDescription(oldAlarm,times);
                break;
            case "ASSET":
                adjustAssetDescription(oldAlarm,times);
                break;
            case "BEHAVIOUR":
                adjustBehaviourDescription(oldAlarm,times);
                break;
            case "DNSRELATE":
                adjustDnsRelateDescription(oldAlarm,times);
                break;
            case "CERRELATE":
                adjustCerRelateDescription(oldAlarm,times);
                break;
            case "CERT":
                adjustCertDescription(oldAlarm,times);
                break;
            case "TUPLE":
                adjustTupleDescription(oldAlarm,times);
                break;
            default:
                adjustOtherDescription(oldAlarm,times);
        }
        return oldAlarm;
    }

    /*填装其他告警*/
    private void adjustOtherDescription(Alarm alarm, Integer times) {
        //输出原有描述
    }


    /*填装五元组告警*/
    private void adjustTupleDescription(Alarm alarm, Integer times) {
        StringBuilder description = new StringBuilder();
        fixPrefixTime(description,alarm);
        int secure = properties.getSecure();
        switch (secure){
            case 1:
                //安全
                description.append(times).append("次命中");
                if (null != alarm.getTask()) {
                    description.append(alarm.getTask()).append("提供的");
                }
                description.append("五元组规则，检测到").append(alarm.getProName()).append("通信中")
                        .append(alarm).append("线索");
                break;
            case 2:
                //ZC
                description.append(times).append("次检测到五元组的").append(alarm.getCategoryDesc()).append("通信");
                fixServerCountry(alarm,description);
        }
        alarm.setDescription(description.toString());
    }

    /*填装证书告警 不合并只填装description*/
    private void adjustCertDescription(Alarm alarm, Integer times) {
        String categoryDesc = alarm.getCategoryDesc();
        StringBuilder description = new StringBuilder();
        if ("证书".equals(categoryDesc)) {
            String sha1 = alarm.getSha1();
            fixPrefixTime(description,alarm);
            description.append("检测到sha1为").append(sha1);
            String subCategory = alarm.getSubCategory();
            if ("leak".equals(subCategory)) {
                description.append("的证书存在算法漏洞");
            } else if ("unreliability_cert".equals(subCategory)) {
                description.append("的证书不可靠");
            } else if ("incompliance_cert".equals(subCategory)) {
                description.append("的证书不合规");
            } else if ("selfsigned_cert".equals(subCategory)) {
                description.append("的证书为“*.gov.cn”自签名证书");
            } else if ("signaturealgooid".equals(subCategory)) {
                description.append("国密证书（").append(alarm.getSubCategoryDesc()).append("）");
            } else {
                description.append("的可疑证书");
            }
        } else {
            description.append(alarm.getDescription());
        }
        alarm.setDescription(description.toString());
    }

    /*填装证书关联告警*/
    private void adjustCerRelateDescription(Alarm alarm, Integer times) {
        StringBuilder description = new StringBuilder();
        fixPrefixTime(description,alarm);
        description.append(times).append("次").append("命中");
        String subCategory = alarm.getSubCategory();
        if ("leak".equals(subCategory)){
            fixTask(description,alarm);
            description.append("检测到").append(alarm.getProName()).append("通信中")
                    .append(alarm.getSubCategoryDesc())
                    .append("（").append(alarm.getCategoryDesc())
                    .append("）").append(alarm.getAccuracy())
                    .append("线索，发现");
        }else{
            description.append("发现");
            fixAssetName(description,alarm);
            fixAssetIp(description,alarm);
            description.append("使用 sha1为").append(alarm.getSha1()).append("的");
            switch (subCategory){
                case "unreliability_cert":
                    description.append("不可信");
                    try {
                        String remark = alarm.getRemark();
                        JSONObject jsonObject = JSONObject.parseObject(remark);
                        List<String> reliabilitydetail = (List<String>) jsonObject.getOrDefault("reliabilitydetail",new ArrayList<>());
                        for (String detail : reliabilitydetail) {
                            if (!detail.isEmpty()) {
                                description.append("[").append(detail).append("]");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "incompliance_cert":
                    description.append("不合规");
                    JSONObject jsonObject = JSONObject.parseObject(alarm.getRemark());
                    List<String> unreliabilityDetail = (List) jsonObject.getOrDefault("compliancedetail", new ArrayList<>());
                    for (String detail : unreliabilityDetail) {
                        if (!detail.isEmpty()) {
                            description.append("[").append(detail).append("]");
                        }
                    }
                    break;
                case "selfsigned_cert":
                    description.append("自签名*.gov.cn");
                    break;
                default:
                    description.append("可疑");
            }
            description.append("证书通联会话");
            fixServerCountry(alarm,description);
        }
        alarm.setDescription(description.toString());
    }

    /*填装dns关联告警描述*/
    private void adjustDnsRelateDescription(Alarm alarm, Integer times) {
        StringBuilder stringBuilder = new StringBuilder();
        fixPrefixTime(stringBuilder,alarm);
        stringBuilder.append("次");
        fixTask(stringBuilder,alarm);
        stringBuilder.append("检测到").append(alarm.getProName()).append("通信中")
                .append(alarm.getSubCategoryDesc()).append("（")
                .append(alarm.getCategoryDesc()).append("）线索，发现");
        fixAssetName(stringBuilder,alarm);
        stringBuilder.append(alarm.getAssetIp())
                .append("的资产外联会话信息。");
        alarm.setDescription(stringBuilder.toString());
    }

    /*填装行为告警描述*/
    private void adjustBehaviourDescription(Alarm alarm,Integer times) {
        String accuracyDesc = "疑似".equals(alarm.getAccuracy()) ? "可疑" : "";
        StringBuilder stringBuilder = new StringBuilder();
        fixPrefixTime(stringBuilder,alarm);
        if (StringUtils.isNotEmpty(alarm.getAssetName())) {
            stringBuilder.append("在").append(alarm.getAssetName()).append("通信数据中");
        }
        stringBuilder.append("检测到").append(times).append("次")
                .append(alarm.getSubCategoryDesc())
                .append("（").append(alarm.getCategoryDesc()).append("）")
                .append(accuracyDesc).append("行为");
        alarm.setDescription(stringBuilder.toString());
    }

    /*填装asset描述*/
    private void adjustAssetDescription(Alarm alarm,Integer times) {
        StringBuilder stringBuilder = new StringBuilder();
        fixPrefixTime(stringBuilder,alarm);
        if (StringUtils.isNotEmpty(alarm.getAssetName())) {
            stringBuilder.append("在").append(alarm.getAssetName()).append("通信数据中");
        }
        stringBuilder.append("检测到").append(times).append("次")
                .append(alarm.getSubCategoryDesc())
                .append("（").append(alarm.getCategoryDesc()).append("）").append("通信");
        alarm.setDescription(stringBuilder.toString());
    }

    /*填装dw描述*/
    private void adjustDWDescription(Alarm oldAlarm, Integer times) {
        int secure = properties.getSecure();
        StringBuilder stringBuilder = new StringBuilder();
        switch (secure){
            case 1:
                fixDWSecure(stringBuilder,oldAlarm,times);
                break;
            case 2:
                fixPrefixTime(stringBuilder,oldAlarm);
                stringBuilder.append(times).append("次检测到")
                        .append(oldAlarm.getUserId())
                        .append("的").append(oldAlarm.getSubCategoryDesc())
                        .append("（").append(oldAlarm.getCategoryDesc()).append("）").append("通信，");
                fixServerCountry(oldAlarm,stringBuilder);
        }
        oldAlarm.setDescription(stringBuilder.toString());
    }

    /**
     * 填装安全的属性
     */
    private void fixDWSecure(StringBuilder description,Alarm oldAlarm,Integer times){
        fixPrefixTime(description,oldAlarm);
        fixAssetName(description,oldAlarm);
        description.append("检测到").append(times).append("次").append(oldAlarm.getAccuracy()).append(oldAlarm.getSubCategoryDesc()).append("（").append(oldAlarm.getCategoryDesc()).append("）通信");
    }

    /**
     * 设置前面的时间  形如：2020-05-14 13:38:13[一般]:
     */
    private void fixPrefixTime(StringBuilder description,Alarm alarmMaterialData){
        description.append(DateUtils.format(alarmMaterialData.capTime));
        description.append("[").append(alarmMaterialData.getLevel())
                .append("]：");
    }

    /*填装src描述*/
    private void adjustSrcDescription(Alarm oldAlarm, Integer times){
        int secure = properties.getSecure();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DateUtils.format(oldAlarm.capTime))
                .append("[").append(oldAlarm.getLevel())
                .append("]：").append(times).append("次");
        switch (secure){
            //安全
            case 1:
                fixTask(stringBuilder,oldAlarm);
                stringBuilder.append("检测到");
                fixAssetName(stringBuilder,oldAlarm);
                stringBuilder.append(oldAlarm.getProName()).append("通信中").append(oldAlarm.getSubCategoryDesc()).append("（")
                        .append(oldAlarm.getCategoryDesc()).append("）").append(oldAlarm.getAccuracy()).append("线索");
                break;
            //ZC
            case 2:
                stringBuilder.append(times).append("次检测到").append(oldAlarm.getUserId())
                        .append("的").append(oldAlarm.getAccuracy())
                        .append(oldAlarm.getSubCategoryDesc()).append("（")
                        .append(oldAlarm.getCategoryDesc()).append("）通信");
                fixServerCountry(oldAlarm,stringBuilder);

        }
        oldAlarm.setDescription(stringBuilder.toString());
    }

    private void fixServerCountry(Alarm oldAlarm,StringBuilder stringBuilder){
        Location serverLocation = oldAlarm.getServerLocation();
        if (Objects.isNull(serverLocation)){
            return;
        }
        if (!"other".equals(serverLocation.getCountry())){
            stringBuilder.append(",服务端位于").append(serverLocation.getCountry());
        }
    }

    private void fixTask(StringBuilder description, Alarm oldAlarm) {
        if (!oldAlarm.getIsSystem()) {
            description.append("命中");
            if (null != oldAlarm.getTask() && !oldAlarm.getTask().isEmpty()) {
                description.append(oldAlarm.getTask()).append("提供的");
            }
            description.append(oldAlarm.getTitle()).append("，");
        }
    }

    /**
     * 填装assetName  形如:【assetName】通信数据中
     */
    private void fixAssetName(StringBuilder description, Alarm oldAlarm) {
        if (StringUtils.isNotEmpty(oldAlarm.getAssetName())) {
            description.append(oldAlarm.getAssetName()).append("通信数据中");
        }
    }

    /**
     * 填装assetIp
     */
    private void fixAssetIp(StringBuilder description, Alarm alarm) {
        if (null != alarm.getAssetIp()) {
            description.append(alarm.getAssetIp());
        }
    }

    /**
     * 告警信息的合并  10分钟内的数据合并   如果超过时间返回null
     * 合并时间信息和eventData信息
     * @param pattern 告警类型
     * @param newAlarm 当前数据
     * @param oldAlarm map集合中的数据 （第一条）
     * @return 合并后的数据
     */
    private synchronized Alarm mergeAlarm(Integer pattern, Alarm newAlarm, Alarm oldAlarm) {
        //10分钟的间隔
        int time = DateUtils.SECOND * DateUtils.MINUTE * 10;
        //超时返回null  不合并
        if (overTime(time,newAlarm,oldAlarm)){
            return null;
        }
        //需要合并时 先记录后一条记录的endtime的值 合并到第一条记录
        oldAlarm.setDurationEndTime(newAlarm.getDurationTime() + newAlarm.getCapTime());

        //合并eventData
        List<String> eventData = oldAlarm.getEventData();
        eventData.addAll(newAlarm.getEventData());
        oldAlarm.setEventData(eventData);

        return oldAlarm;
    }




    /**
     * 是否超时
     * @param time 规定的时间
     * @param newAlarm 当前数据
     * @param oldAlarm map集合中的数据 （第一条）
     * @return 是否超时
     */
    private Boolean overTime(int time, Alarm newAlarm, Alarm oldAlarm){
        long endTime = oldAlarm.getCapTime() + oldAlarm.getDurationTime();
        Long startTime = newAlarm.getCapTime();
        return Math.abs(endTime - startTime) > time;
    }

    /**
     * 根据告警类型截取告警的key 用来确定该告警的唯一标识 存放进map
     * @param pattern 0.cert 证书告警
     *               1.src src告警 2.dw dw告警 3.asset 资产告警
     *               4.behaviour 行为告警 5.dns_related DNS关联告警 6.cert_related 证书关联告警
     *               7.tuple 五元组 8.other 特殊告警
     * @param alarmMaterialData
     * @return
     */
    private String getAlarmKeyByPattern(Integer pattern,AlarmMaterialData alarmMaterialData){
        StringBuilder stringBuilder = new StringBuilder();
        String serverIp = alarmMaterialData.getServerIp();
        String clientIp = alarmMaterialData.getClientIp();
        String categoryDesc = alarmMaterialData.getCategoryDesc();
        String subCategoryDesc = alarmMaterialData.getSubCategoryDesc();
        String title = alarmMaterialData.getTitle();
        switch (pattern){
            case 0:
                //证书告警 不对告警进行操作
                return null;
            case 1: case 5: case 7: case 8:
                //src告警 /DNS关联告警 /五元组 /特殊告警
                stringBuilder.append(clientIp)
                        .append(serverIp)
                        .append(categoryDesc)
                        .append(subCategoryDesc)
                        .append(title);
                break;
            case 2:
                //dw告警
                stringBuilder.append(clientIp).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
            case 3:
                //资产告警
                stringBuilder.append(alarmMaterialData.getAssetIp()).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
            case 4: case 6:
                //行为告警 /证书关联告警
                stringBuilder.append(serverIp).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
        }
        return stringBuilder.toString();
    }

    @Override
    public void init() {

    }
}
