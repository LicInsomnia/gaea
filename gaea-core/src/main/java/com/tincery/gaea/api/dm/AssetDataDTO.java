package com.tincery.gaea.api.dm;

import com.tincery.gaea.core.base.tool.util.NumberUtils;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class AssetDataDTO extends SimpleBaseDO {

    @Id
    private String id;
    private String unit;
    private String ip;
    private String name;
    private String proname;
    private Integer port;
    private Long byteNum;
    private Long downByte;
    private Long downPkt;
    private Boolean alarm;
    private Integer heat;
    private String key;
    private Long pkt;
    private Long sessionCount;
    private LocalDateTime timeTag;
    private Long upByte;
    private Long upPkt;
    private List<AssetClient> clients;
    private Map<String, Object> extensions;

    @Setter
    @Getter
    private static class AssetClient {
        private String clientIp;
        private String country;
        private boolean foreign;
        private Long value;

    }

    /****
     * 两个资产信息合并
     **/
    public AssetDataDTO merge(AssetDataDTO that) {
        this.byteNum = NumberUtils.sum(that.byteNum, this.byteNum);
        this.alarm = this.alarm || that.alarm;
        this.downByte = NumberUtils.sum(that.downByte, this.downByte);
        this.downPkt = NumberUtils.sum(that.downPkt, this.downPkt);
        this.pkt = NumberUtils.sum(this.pkt, that.pkt);
        this.sessionCount = NumberUtils.sum(this.sessionCount, that.sessionCount);
        this.upPkt = NumberUtils.sum(this.upPkt, that.upPkt);
        this.upByte = NumberUtils.sum(this.upByte, that.upByte);
        /*if(CollectionUtils.isEmpty(this.clients) && CollectionUtils.isEmpty(that.clients)){
            return this;
        }
*/
        return this;
    }


    /****
     * json来的时候只有具体数据  有些字段需要我们自己通过json数据进行填充调整
     * 比如 pkt = unpkt+downpkt
     **/
    public AssetDataDTO adjust() {
        this.pkt = NumberUtils.sum(this.upPkt, this.downPkt);
        this.byteNum = NumberUtils.sum(this.upByte, this.downByte);
        return this;
    }


}
