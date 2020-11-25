package com.tincery.gaea.api.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.dm.AssetConfigDO;
import com.tincery.gaea.api.src.extension.AlarmExtension;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.tool.ToolUtils;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 告警素材元数据类
 *
 * @author Insomnia
 * @version 1.0.2
 * @date 2020/03/21
 */
@Setter
@Getter
@ToString
public final class AlarmMaterialData {
    /**
     * 探针标识 记录是哪个探针生成的txt
     */
    public String source;
    /**
     * 数据采集时间
     */
    public Long capTime;
    /**
     * 用户标识
     */
    protected String userId;
    /**
     * 服务端标识
     */
    protected String serverId;
    /**
     * 协议名
     */
    protected String proName;
    /**
     * 是否加密
     */
    protected int isEncrypt;
    /**
     * 内外层五元组相关
     */
    protected Integer protocol;
    protected String clientMac;
    protected String serverMac;
    protected String clientIp;
    protected String serverIp;
    protected Integer clientPort;
    protected Integer serverPort;
    protected String clientIpOuter;
    protected String serverIpOuter;
    protected Integer clientPortOuter;
    protected Integer serverPortOuter;
    protected Integer protocolOuter;
    /**
     * 是否重点目标
     */
    protected Boolean imp;
    /**
     * 在伪造协议时上下行字节
     */
    protected String malformedUpPayload;
    protected String malformedDownPayload;
    /**
     * SIM卡唯一识别码
     */
    protected String imsi;
    /**
     * 移动终端唯一识别码
     */
    protected String imei;
    /**
     * 手机号
     */
    protected String msisdn;
    /**
     * dataType数据类型字段
     * session：-1：其他 0：ssl 1：dns
     * dns：-1：伪造 0：请求 1：应答
     * ssl： -1：伪造 0：正常
     * http： -1：伪造 1：正常
     * ssh： -1：伪造  1：正常
     */
    protected Integer dataType;
    /**
     * 组名
     */
    protected String groupName;
    /**
     * 重点目标标识
     */
    protected String targetName;
    /**
     * 流量相关
     */
    protected long upPkt;
    protected long upByte;
    protected long downPkt;
    protected long downByte;
    /**
     * 会话持续时间（默认为0，仅在TCP时有可能会大于0）
     */
    protected long duration;
    /**
     * 这次会话的结束时间（对比与第一条数据的结束时间）。告警合并用
     */
    protected long durationEndTime;
    /**
     * 标签
     */
    protected Set<String> caseTags;
    /**
     * 是否将特殊会话的MAC地址字段转为外层五元组
     */
    protected Boolean macOuter;
    /**
     * 特殊不可控字段，含预留信息
     */
    protected Map<String, Set<Object>> specialElement;

    private Location serverLocation;
    private Location clientLocation;
    private Location serverLocationOuter;
    private Location clientLocationOuter;

    private String orgLink;
    private Boolean isSystem;
    private Integer type;
    private String ruleName;
    private String createUser;
    private Set<String> viewUsers;
    private Integer category;
    private String categoryDesc;
    private String subCategory;
    private String subCategoryDesc;
    private String title;
    private Integer level;
    private String task;
    private String eventData;
    private String remark;
    private String key;
    private String context;
    private Integer checkMode;
    private Integer accuracy;
    private String description;
    private String publisher;
    /**
     * 告警资产信息相关
     */
    private String assetUnit;
    private String assetIp;
    private String assetName;
    private Integer assetLevel;
    private String assetType;
    /**
     * 证书告警专用
     */
    private String sha1;

    private Map<String, Object> extension;


    public AlarmMaterialData() {
    }


    /****
     * 这里不用各种设计模式和工具 为了速度
     * 因为gaea整体相关方法需要调用100W+次  需要为了速度付出一些代码量的代价
     * 此写法比hutool工具的BeanUtils速度快800倍以上
     * 比fastjson转换快1500倍以上
     **/
    public AlarmMaterialData(AbstractMetaData metaData) {
        this.targetName = metaData.getTargetName();
        this.groupName = metaData.getGroupName();
        this.userId = metaData.getUserId();
        this.serverId = metaData.getServerId();
        this.capTime = metaData.getCapTime();
        this.protocol = metaData.getProtocol();
        this.proName = metaData.getProName();
        this.clientMac = metaData.getClientMac();
        this.serverMac = metaData.getServerMac();
        this.clientIp = metaData.getClientIp();
        this.serverIp = metaData.getServerIp();
        this.clientPort = metaData.getClientPort();
        this.serverPort = metaData.getServerPort();
        this.clientIpOuter = metaData.getClientIpOuter();
        this.serverIpOuter = metaData.getServerIpOuter();
        this.clientPortOuter = metaData.getClientPortOuter();
        this.serverPortOuter = metaData.getServerPortOuter();
        this.eventData = metaData.getEventData();
        this.source = metaData.getSource();
        this.upPkt = metaData.getUpPkt();
        this.downPkt = metaData.getDownPkt();
        this.upByte = metaData.getUpByte();
        this.downByte = metaData.getDownByte();
        this.duration = metaData.getDuration();
        this.durationEndTime = metaData.getCapTime() + metaData.getDuration();
    }

    /**
     * 资产需要的一个新的告警
     */
    public AlarmMaterialData(JSONObject jsonObject,AssetConfigDO assetConfigDO,Boolean isClient){
        this.source = jsonObject.getString("source");
        this.capTime = DateUtils.validateTime(jsonObject.getLong("capTime"));
        this.duration = jsonObject.getLong("duration");

        this.userId = jsonObject.getString("userId");
        this.serverId = jsonObject.getString("serverId");

        this.clientIp = jsonObject.getString("clientIp");
        this.clientLocation = JSON.parseObject(jsonObject.getString("clientLocation"),Location.class);
        this.serverIp = jsonObject.getString("serverIp");
        this.serverLocation = JSON.parseObject(jsonObject.getString("serverLocation"),Location.class);
        this.clientPort = jsonObject.getInteger("clientPort");
        this.serverPort = jsonObject.getInteger("serverPort");
        this.protocol = jsonObject.getInteger("protocol");
        this.proName = jsonObject.getString("proName");
        this.isSystem = true;
        this.createUser = "system";

        this.level = 2;
        this.categoryDesc = "资产告警";
        this.setType(5);
        this.setCategory(11);
        this.setAccuracy(1);
        this.setCheckMode(8);
        this.assetLevel = assetConfigDO.getLevel();
        this.assetName = assetConfigDO.getName();
        this.assetUnit = assetConfigDO.getUnit();
        this.assetType = assetConfigDO.getType();
        if (isClient){
            this.assetIp = this.clientIp;
        }else{
            this.assetIp = this.serverIp;
        }
        /*填装eventData*/
        if (Objects.nonNull(jsonObject.getString("alarmKey"))){
            this.eventData = jsonObject.getString("alarmKey");
        }else {
            String md5 = ToolUtils.getMD5(jsonObject.toJSONString());
            jsonObject.put("alarmKey",md5);
            this.eventData = md5;
        }
    }

    private void fixAssetIp(Integer assetFlag) {
        switch (assetFlag){
            case 1:
                //CLIENT_ASSET
                this.assetIp = this.clientIp;
                break;
            case 2:
                //SERVER_ASSET
                this.assetIp = this.serverIp;
                break;
            case 3:
                //CLIENT_ASSET & SERVER_ASSET
                this.assetIp = this.clientIp + ";" + this.serverIp;
                break;
        }
    }

    public AlarmMaterialData(AbstractMetaData metaData, SrcRuleDO alarmRule, String context, IpSelector ipSelector) {
        this.targetName = metaData.getTargetName();
        this.groupName = metaData.getGroupName();
        this.userId = metaData.getUserId();
        this.serverId = metaData.getServerId();
        this.capTime = metaData.getCapTime();
        this.protocol = metaData.getProtocol();
        this.proName = metaData.getProName();
        this.clientMac = metaData.getClientMac();
        this.serverMac = metaData.getServerMac();
        this.clientPort = metaData.getClientPort();
        this.serverPort = metaData.getServerPort();
        this.clientPortOuter = metaData.getClientPortOuter();
        this.serverPortOuter = metaData.getServerPortOuter();
        this.eventData = metaData.getEventData();
        this.source = metaData.getSource();
        this.ruleName = alarmRule.getRuleName();
        this.createUser = alarmRule.getCreateUser();
        this.category = alarmRule.getGaeaFlag();
        this.categoryDesc = alarmRule.getCategory();
        this.subCategory = alarmRule.getMatchField();
        this.subCategoryDesc = alarmRule.getSubcategory();
        this.type = alarmRule.getType();
        this.title = alarmRule.getRuleValue();
        this.level = alarmRule.getLevel();
        this.task = alarmRule.getTask();
        if (null != alarmRule.getViewUsers()) {
            this.viewUsers = new HashSet<>(alarmRule.getViewUsers());
        }
        this.remark = alarmRule.getRemark();
        this.orgLink = alarmRule.getOrgLink();
        this.checkMode = alarmRule.getMode();
        this.context = context;
        this.accuracy = alarmRule.getAccuracy();
        this.isSystem = alarmRule.getIsSystem();
        List<String> caseTags = alarmRule.getCaseTags();
        if (!CollectionUtils.isEmpty(caseTags)) {
            if (this.caseTags == null) {
                this.caseTags = new HashSet<>();
            }
            this.caseTags.addAll(caseTags);
        }
        this.publisher = alarmRule.getPublisher();

        if((this.clientIp = metaData.getClientIp())!=null){
            this.clientLocation = ipSelector.getCommonInformation(this.clientIp);
        }
        if((this.serverIp = metaData.getServerIp())!=null){
            this.serverLocation = ipSelector.getCommonInformation(this.serverIp);
        }
        if((this.serverIpOuter = metaData.getServerIpOuter())!=null){
            this.serverLocationOuter = ipSelector.getCommonInformation(this.serverIpOuter);
        }
        if((this.clientIpOuter = metaData.getClientIpOuter())!=null){
            this.clientLocationOuter = ipSelector.getCommonInformation(this.clientIpOuter);
        }





    }

    public void appendExtension(AlarmExtension alarmExtension) {
        this.orgLink = alarmExtension.getOrgLink();
        this.isSystem = alarmExtension.getIsSystem();
        this.type = alarmExtension.getType();
        this.ruleName = alarmExtension.getRuleName();
        this.createUser = alarmExtension.getCreateUser();
        this.viewUsers = alarmExtension.getViewUsers();
        this.category = alarmExtension.getCategory();
        this.categoryDesc = alarmExtension.getCategoryDesc();
        this.subCategory = alarmExtension.getSubCategory();
        this.subCategoryDesc = alarmExtension.getSubCategoryDesc();
        this.title = alarmExtension.getTitle();
        this.level = alarmExtension.getLevel();
        this.task = alarmExtension.getTask();
        this.remark = alarmExtension.getRemark();
        this.checkMode = alarmExtension.getCheckMode();
        this.accuracy = alarmExtension.getAccuracy();
        this.publisher = alarmExtension.getPublisher();
    }

    public void setKey() {
        Object[] join = {this.userId, this.serverId, this.createUser, this.categoryDesc, this.subCategoryDesc, this.title, this.capTime};
        String joinString = Joiner.on(";").useForNull("").join(join);
        this.key = ToolUtils.getMD5(joinString);
    }

    public final void setKey(String keyStr) {
        if (keyStr == null) {
            setKey();
        } else {
            this.key = keyStr;
        }
    }


    public final void merge(AlarmMaterialData alarmMaterialData) {
        long minCaptime = Math.min(this.capTime, alarmMaterialData.capTime);
        long endTime = Math.max(this.capTime + this.duration, alarmMaterialData.capTime + alarmMaterialData.duration);
        this.capTime = minCaptime;
        this.duration = endTime - capTime;
    }


    public final void setAsset(AssetDetector assetDetector) {
        AssetConfigDO asset = null;
        String singleAssetIp = null;
        if (assetDetector.checkAssetIp(this.serverIp)) {
            asset = assetDetector.getAsset(this.serverIp);
            singleAssetIp = this.serverIp;
        } else if (assetDetector.checkAssetIp(this.clientIp)) {
            asset = assetDetector.getAsset(this.clientIp);
            singleAssetIp = this.clientIp;
        }
        if (null == asset) {
            return;
        }
        this.assetIp = singleAssetIp;
        //   this.assetInfo = new Document((JSONObject) JSONObject.toJSON(asset.getInfo()));
    }

}
