package com.tincery.gaea.api.dm.alarm;

import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.base.Location;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@Document(collection = "alarm")
public class Alarm extends SimpleBaseDO {

    @Id
    String id;
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
    private String type;
    private String ruleName;
    private String createUser;
    private Set<String> viewUsers;
    private String category;
    private String categoryDesc;
    private String subCategory;
    private String subCategoryDesc;
    private String title;
    private String level;
    private String task;
    private List<String> eventData;
    private String remark;
    private String key;
    private String context;
    private String checkMode;
    private String accuracy;
    private String description;
    private String pattern;
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
    /**
     * 标识句柄，用于告警统计，默认入库为false，告警统计处理后变为true
     */
    private Boolean handle = false;

    /**
     * 码表转换 category int->String
     * level int -> String
     * checkmode int -> String
     * function int -> String
     * accuracy int -> String
     * range int -> String
     * type int -> String
     * pattern int -> String
     *
     *
     * @param alarmMaterialData
     */
    public Alarm(AlarmMaterialData alarmMaterialData){
        this.source = alarmMaterialData.getSource();
        this.capTime = alarmMaterialData.getCapTime();
        this.targetName = alarmMaterialData.getTargetName();
        this.groupName = alarmMaterialData.getGroupName();
        this.imsi = alarmMaterialData.getImsi();
        this.imei = alarmMaterialData.getImei();
        this.msisdn = alarmMaterialData.getMsisdn();
        this.dataType = alarmMaterialData.getDataType();
        this.context = alarmMaterialData.getContext();
        this.task = alarmMaterialData.getTask();
        this.subCategory = alarmMaterialData.getSubCategory();
        this.subCategoryDesc = alarmMaterialData.getSubCategoryDesc();
        this.categoryDesc = alarmMaterialData.getCategoryDesc();
        this.protocol = alarmMaterialData.getProtocol();
        this.description = alarmMaterialData.getDescription();
        this.clientIp = alarmMaterialData.getClientIp();
        this.serverIp = alarmMaterialData.getServerIp();
        this.serverMac = alarmMaterialData.getServerMac();
        this.clientPort = alarmMaterialData.getClientPort();
        this.serverPort = alarmMaterialData.getServerPort();
        this.ruleName = alarmMaterialData.getRuleName();
        this.key = alarmMaterialData.getKey();
        this.clientMac = alarmMaterialData.getClientMac();
        this.title = alarmMaterialData.getTitle();
        this.assetIp = alarmMaterialData.getAssetIp();
        this.assetName = alarmMaterialData.getAssetName();
        this.assetUnit = alarmMaterialData.getAssetUnit();
        this.proName = alarmMaterialData.getProName();
        this.remark = alarmMaterialData.getRemark();
        this.userId = alarmMaterialData.getUserId();
        this.isSystem = alarmMaterialData.getIsSystem();
        this.publisher = alarmMaterialData.getPublisher();
        this.createUser = alarmMaterialData.getCreateUser();
        this.orgLink = alarmMaterialData.getOrgLink();
        this.sha1 = alarmMaterialData.getSha1();
        this.serverId = alarmMaterialData.getServerId();
        this.isEncrypt = alarmMaterialData.getIsEncrypt();
        this.clientIpOuter = alarmMaterialData.getClientIpOuter();
        this.serverIpOuter = alarmMaterialData.getServerIpOuter();
        this.clientPortOuter = alarmMaterialData.getClientPortOuter();
        this.serverPortOuter = alarmMaterialData.getServerPortOuter();
        this.protocolOuter = alarmMaterialData.getProtocolOuter();
        this.imp = alarmMaterialData.getImp();
        this.malformedUpPayload = alarmMaterialData.getMalformedUpPayload();
        this.malformedDownPayload = alarmMaterialData.getMalformedDownPayload();
        this.upPkt = alarmMaterialData.getUpPkt();
        this.upByte = alarmMaterialData.getUpByte();
        this.downPkt = alarmMaterialData.getDownPkt();
        this.downByte = alarmMaterialData.getDownByte();
        this.duration = alarmMaterialData.getDuration();
        this.durationEndTime = alarmMaterialData.getDurationEndTime();
        this.caseTags = alarmMaterialData.getCaseTags();
        this.macOuter = alarmMaterialData.getMacOuter();
        this.specialElement = alarmMaterialData.getSpecialElement();
        this.serverLocation = alarmMaterialData.getServerLocation();
        this.clientLocation = alarmMaterialData.getClientLocation();
        this.serverLocationOuter = alarmMaterialData.getServerLocationOuter();
        this.clientLocationOuter = alarmMaterialData.getClientLocationOuter();
        this.viewUsers = alarmMaterialData.getViewUsers();
        this.extension = alarmMaterialData.getExtension();
    }

}
