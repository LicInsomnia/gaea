package com.tincery.gaea.api.dm;

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
    private Map<String,Object> extensions;

    @Setter
    @Getter
    private static class AssetClient{
        private String clientIp;
        private String country;
        private boolean foreign;
        private Long value;
    }




}
