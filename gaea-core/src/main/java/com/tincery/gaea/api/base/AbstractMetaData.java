package com.tincery.gaea.api.base;


import com.google.common.base.Joiner;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.tool.util.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/**
 * 基础元数据类
 *
 * @author gongxuanzhang
 */
@Getter
@Setter
@ToString
public abstract class AbstractMetaData extends GaeaData {
    /**
     * 探针标识 记录是哪个探针生成的txt
     */
    public String source;
    /**
     * 数据采集时间
     */
    public Long capTime;
    /** 用户标识 */
    protected String userId;
    /** 服务端标识 */
    protected String serverId;
    /** 协议名 */
    protected String proName;
    /** 内外层五元组相关 */
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
     * dataType数据类型字段 session：0：未知proName 1：已知proName dns：-1：伪造 0：请求 1：应答 ssl： -1：伪造 0：正常 http： -1：伪造 1：正常 ssh： -1：伪造
     * 1：正常
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
     * SYN标识（TCP协议会话建联标识）
     */
    protected Boolean syn;
    /**
     * FIN标识（TCP协议会话结束标识）
     */
    protected Boolean fin;
    /**
     * 该会话是否境外会话
     */
    protected Boolean foreign;

    @Field()
    protected String eventData;

    protected Set<String> caseTags;

    public String getDateSetFileName(String category) {
        return FileUtils.getCsvDataFile(category, this.capTime, NodeInfo.getNodeName());
    }

    public abstract void adjust();

    /**
     * @param splitChar csv分隔符
     * @return csv数据行
     */
    protected String toCsv(char splitChar) {
        Object[] join = new Object[]{this.source, this.capTime};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
