package com.tincery.gaea.api.base;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.tool.ToolUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 告警素材元数据类
 *
 * @author Insomnia
 * @version 1.0.2
 * @date 2020/03/21
 */
@Setter
@Getter
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
    protected long durationTime;
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
    private Integer pattern;
    private String publisher;
    private String assetIp;
    private Document assetInfo;
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
        this.serverLocation = ipSelector.getCommonInformation(this.serverIp);
        this.serverLocation = ipSelector.getCommonInformation(this.serverIp);
        this.serverLocationOuter = ipSelector.getCommonInformation(this.serverIpOuter);
        this.clientLocationOuter = ipSelector.getCommonInformation(this.clientIpOuter);
    }


    private void setKey() {
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
        long endTime = Math.max(this.capTime + this.durationTime, alarmMaterialData.capTime + alarmMaterialData.durationTime);
        this.capTime = minCaptime;
        this.durationTime = endTime - capTime;
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
        this.assetInfo = new Document((JSONObject) JSONObject.toJSON(asset.getInfo()));
    }

}
