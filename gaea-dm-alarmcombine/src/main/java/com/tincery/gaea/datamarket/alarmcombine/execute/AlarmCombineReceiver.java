package com.tincery.gaea.datamarket.alarmcombine.execute;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.dm.alarm.Alarm;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.dao.alarm.AlarmDao;
import com.tincery.gaea.core.base.mgt.AlarmDictionary;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.dm.AbstractDataMarketReceiver;
import com.tincery.gaea.core.dm.DmProperties;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sparrow
 */
@Slf4j
@Service
public class AlarmCombineReceiver extends AbstractDataMarketReceiver {

    @Autowired
    private AlarmDictionary alarmDictionary;
    @Autowired
    private AlarmDao alarmDao;

    @Override
    @Autowired
    protected void setDmProperties(DmProperties dmProperties) {
        this.dmProperties = dmProperties;
    }

    /**
     * @param textMessage 接收到的消息  存放要扫描的目录名
     * @throws JMSException
     * 扫描的文件夹NodeInfo.getAlarmMaterial
     */
    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        log.info("alarmCombine接收到了消息开始处理");
        ConcurrentHashMap<String, Pair<Alarm,Integer>> alarmMap = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<Alarm> resultList = new CopyOnWriteArrayList<>();
        File[] fileList = getFileList();
        if (Objects.isNull(fileList)){
            return;
        }
        analysisFileList(fileList,alarmMap,resultList);
        //输出告警Alarm
        free(resultList,alarmMap);
    }

    /**
     * 解析文件列表 并把数据填入到两个集合中
     * @param fileList 文件列表
     * @param alarmMap 需要合并的集合
     * @param resultList 输出的结果集合
     */
    private void analysisFileList(File[] fileList,ConcurrentHashMap<String, Pair<Alarm,Integer>> alarmMap,CopyOnWriteArrayList<Alarm> resultList) {
        log.info("开始解析文件,共[{}]个文件", fileList.length);
        for (File file : fileList) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                log.info("开始解析文件[{}]", file.getName());
                while ((line = bufferedReader.readLine()) != null) {
                    AlarmMaterialData alarmMaterialData;
                    Alarm newAlarm;
                    try {
                        alarmMaterialData = JSON.parseObject(line).toJavaObject(AlarmMaterialData.class);
                        newAlarm = fixAlarm(alarmMaterialData);
                    } catch (JSONException e) {
                        log.error("数据解析错误[{}],数据为:[{}]", file.getName(), line);
                        continue;
                    }

                    String pattern = getPatternByCategoryAndSubCategory(alarmMaterialData.getCategory(),
                            alarmMaterialData.getSubCategory());
                    /* 这里的warning是因为pattern还没能完全写完确定 */
                    if (pattern == null) {
                        //证书告警不合并
                        resultList.add(newAlarm);
                    }
                    String key = getAlarmKeyByPattern(pattern, alarmMaterialData);
                    if (StringUtils.isEmpty(key)) {
                        continue;
                    }
                    fixAlarmMapAndResultList(alarmMap, resultList, newAlarm, key);
                }
                //读取完成删除文件
                if (file.exists() && file.isFile()) {
                    this.freeFile(file);
                }
            } catch (IOException | NullPointerException e) {
                log.error("文件解析错误[{}]", file.getName());
            }
        }
    }

    /**
     * 对容器中的数据进行装填和处理
     * @param alarmMap 临时Map 用来合并
     * @param resultList 结果集合 用来输出
     * @param newAlarm 新的实体对象
     * @param key 根据告警素材得到的key
     */
    private void fixAlarmMapAndResultList(ConcurrentHashMap<String, Pair<Alarm, Integer>> alarmMap, CopyOnWriteArrayList<Alarm> resultList, Alarm newAlarm, String key) {
        /* 合并map */
        alarmMap.merge(key,new Pair<>(newAlarm,1),(oldValue,newValue)->{
            Alarm oldAlarmTemp = oldValue.getKey();
            Alarm newAlarmTemp = newValue.getKey();
            Alarm merge = mergeAlarm(newAlarmTemp, oldAlarmTemp);
            if (Objects.isNull(merge)){
                //如果合并失败 分别存储
                resultList.add(adjustDescription(oldAlarmTemp,oldValue.getValue()));
                return newValue;
            }else{
                return new Pair<>(merge,newValue.getValue() + 1);
            }
        });
    }

    /**
     * 根据告警素材生成一个告警实体
     * @param alarmMaterialData 告警素材
     * @return 数据实体
     */
    private Alarm fixAlarm(AlarmMaterialData alarmMaterialData) {
        Alarm newAlarm = new Alarm(alarmMaterialData);
        adjustAlarm(newAlarm, alarmMaterialData);
        return newAlarm;
    }
    /**
     * 获得指定位置的文件夹（路径由配置决定）
     * @return 文件夹下文件的数组
     * @throws JMSException 未能找到文件夹
     */
    private File[] getFileList() throws JMSException {
        String alarmMaterialDir = NodeInfo.getAlarmMaterial();
        File fileFolder = new File(alarmMaterialDir);
        log.info("扫描文件夹[{}]",alarmMaterialDir);
        if (!fileFolder.isDirectory()){
            throw new JMSException("消息错误，请检测配置的文件夹");
        }
        return fileFolder.listFiles();
    }
    /** 根据category和subCategory 确定该条告警属于哪个模块 **/
    private String getPatternByCategoryAndSubCategory(Integer category, String subCategory) {
        if (category<=6 || category == 14){
            return "SRC";
        }
        if (category == 13){
            return "TUPLE";
        }
        if (category == 11){
            return "ASSET";
        }
        //TODO 需要完善
        return "";
    }

    @Override
    protected void dmFileAnalysis(File file) {

    }
    /**
     * 输出告警信息
     * @param resultList 要输出的集合
     * @param alarmMap 还未填装属性的集合
     */
    private void free(CopyOnWriteArrayList<Alarm> resultList, ConcurrentHashMap<String, Pair<Alarm, Integer>> alarmMap) {
        alarmMap.forEach((key,value)-> resultList.add(adjustDescription(value.getKey(),value.getValue())));
        alarmDao.insert(resultList);
        log.info("共入库[{}]条告警信息",resultList.size());
        alarmMap.clear();
        resultList.clear();
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
        //        alarmDictionary.parse("function",alarmMaterialData.getFunction) 元数据没有这个字段
        String accuracy = alarmDictionary.parse("accuracy", alarmMaterialData.getAccuracy());
        alarm.setAccuracy(accuracy);
//        alarmDictionary.parse("range",alarmMaterialData.getRange) 元数据没有这个字段
        String type = alarmDictionary.parse("type", alarmMaterialData.getType());
        alarm.setType(type);
        String checkMode = alarmDictionary.parse("checkmode", alarmMaterialData.getCheckMode());
        alarm.setCheckMode(checkMode);
        ArrayList<String> eventData = new ArrayList<>();
        eventData.add(alarmMaterialData.getEventData());
        alarm.setEventData(eventData);
    }
    /**
     * 填充orDefault的 description
     * @param oldAlarm 原有的数据
     * @param times 命中次数
     * @return alarmMaterialData
     */
    private Alarm adjustDescription(Alarm oldAlarm, Integer times) {
        int category = alarmDictionary.valueOf("category", oldAlarm.getCategory());
        String pattern = getPatternByCategoryAndSubCategory(category, oldAlarm.getSubCategory());
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
                adjustCertDescription(oldAlarm);
                break;
            case "TUPLE":
                adjustTupleDescription(oldAlarm,times);
                break;
            default:
                //输出原有告警
        }
        return oldAlarm;
    }
    /*填装五元组告警*/
    private void adjustTupleDescription(Alarm alarm, Integer times) {
        StringBuilder description = new StringBuilder();
        fixPrefixTime(description,alarm);
        int secure = dmProperties.getSecure();
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
    private void adjustCertDescription(Alarm alarm) {
        String categoryDesc = alarm.getCategoryDesc();
        StringBuilder description = new StringBuilder();
        if ("证书".equals(categoryDesc)) {
            String sha1 = alarm.getSha1();
            fixPrefixTime(description,alarm);
            description.append("检测到sha1为").append(sha1);
            String subCategory = alarm.getSubCategory();
            switch (subCategory){
                case "leak":
                    description.append("的证书存在算法漏洞");
                    break;
                case "unreliability_cert":
                    description.append("的证书不可靠");
                    break;
                case "incompliance_cert":
                    description.append("的证书不合规");
                    break;
                case "selfsigned_cert":
                    description.append("的证书为“*.gov.cn”自签名证书");
                case "signaturealgooid":
                    description.append("国密证书（").append(alarm.getSubCategoryDesc()).append("）");
                default:
                    description.append("的可疑证书");
            }

        } else {
            description.append(alarm.getDescription());
        }
        alarm.setDescription(description.toString());
    }
/*    enum Description{
        leak("","的证书存在算法漏洞",null);

        Description(String pre,String suf,StringBuilder target){

        }
    }*/
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
        stringBuilder.append(times).append("次");
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
        int secure = dmProperties.getSecure();
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
        int secure = dmProperties.getSecure();
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
        if (Objects.isNull(serverLocation)) {
            return;
        }
        if (!HeadConst.PRONAME.OTHER.equals(serverLocation.getCountry())) {
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
     * @param newAlarm 当前数据
     * @param oldAlarm map集合中的数据 （第一条）
     * @return 合并后的数据
     */
    private synchronized Alarm mergeAlarm(Alarm newAlarm, Alarm oldAlarm) {
        //10分钟的间隔
        int time = DateUtils.MINUTE * 10;
        //超时返回null  不合并
        if (overTime(time,newAlarm,oldAlarm)){
            return null;
        }
        //需要合并时 先记录后一条记录的endtime的值 合并到第一条记录
        oldAlarm.setDurationEndTime(newAlarm.getDuration() + newAlarm.getCapTime());
        //合并eventData
        List<String> eventData = oldAlarm.getEventData();
        eventData.addAll(newAlarm.getEventData());
        oldAlarm.setEventData(eventData);

        mergeFlow(oldAlarm,newAlarm);

        return oldAlarm;
    }

    /**
     * 合并流量
     * @param oldAlarm 原有数据
     * @param newAlarm 新的数据
     */
    private void mergeFlow(Alarm oldAlarm, Alarm newAlarm) {
        oldAlarm.setUpByte(oldAlarm.getUpByte() + newAlarm.getUpByte());
        oldAlarm.setDownByte(oldAlarm.getDownByte() + newAlarm.getDownByte());
        oldAlarm.setUpPkt(oldAlarm.getUpPkt() + newAlarm.getUpPkt());
        oldAlarm.setDownPkt(oldAlarm.getDownPkt() + newAlarm.getDownPkt());
    }


    /**
     * 是否超时
     * @param time 规定的时间
     * @param newAlarm 当前数据
     * @param oldAlarm map集合中的数据 （第一条）
     * @return 是否超时
     */
    private Boolean overTime(int time, Alarm newAlarm, Alarm oldAlarm){
        long endTime = oldAlarm.getCapTime() + oldAlarm.getDuration();
        Long startTime = newAlarm.getCapTime();
        return Math.abs(endTime - startTime) > time;
    }

    /**
     * 根据告警类型截取告警的key 用来确定该告警的唯一标识 存放进map
     * @param pattern 0.cert 证书告警
     *               1.src src告警 2.dw dw告警 3.asset 资产告警
     *               4.behaviour 行为告警 5.dns_related DNS关联告警 6.cert_related 证书关联告警
     *               7.tuple 五元组 8.other 特殊告警
     */
    private String getAlarmKeyByPattern(String pattern,AlarmMaterialData alarmMaterialData) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String serverIp = alarmMaterialData.getServerIp();
        String clientIp = alarmMaterialData.getClientIp();
        String categoryDesc = alarmMaterialData.getCategoryDesc();
        String subCategoryDesc = alarmMaterialData.getSubCategoryDesc();
        String title = alarmMaterialData.getTitle();
        switch (pattern){
            case "CERT":
                //证书告警 不对告警进行操作
                return null;
            case "SRC": case "TUPLE":
                //src告警 /DNS关联告警 /五元组 /特殊告警
                stringBuilder.append(clientIp)
                        .append(serverIp)
                        .append(categoryDesc)
                        .append(subCategoryDesc)
                        .append(title);
                break;
            case "DW":
                //dw告警
                stringBuilder.append(clientIp).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
            case "ASSET":
                //资产告警
                stringBuilder.append(alarmMaterialData.getAssetIp()).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
            case "OTHER":
                //行为告警 /证书关联告警
                stringBuilder.append(serverIp).append(categoryDesc).append(subCategoryDesc).append(title);
                break;
            default:
                throw new IOException("没有发现对应的码表值");
        }
        return stringBuilder.toString();
    }

    @Override
    public void init() {

    }



}
