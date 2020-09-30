package com.tincery.gaea.api.dw;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.DnsRequestBO;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class AbstractDataWarehouseData extends AbstractMetaData implements MergeAble {

    String id;
    /**
     * 会话双方地理位置信息
     */
    Location clientLocation;
    Location serverLocation;
    /**
     * 拓展信息根据会话协议有差异
     */
    private String tag;
    private String keyWord;
    private ApplicationInformationBO application;
    private Map<String, ApplicationInformationBO> applicationElements;
    private Set<String> appCheckModes;
    private String checkMode;
    /**
     * 键值参考sys.common_config.reorganization.value.cerkeys
     */
    private JSONObject cer;
    /**
     * 键值参考sys.common_config.reorganization.value.extensionkeys
     */
    private Map<String, Object> extension;
    private DnsRequestBO dnsRequestBO;
    /**
     * 标签信息根据属性抽象
     */
    private String dataSource;
    private Integer dataType;
    private Boolean protocolKnown;
    private Boolean appKnown;
    private Boolean malFormed;
    private Boolean foreign;
    /**
     * 1.特殊应用（{"label.apptype" : "specail"}） 2.重点关注应用（{"label.apptype" : "important") 3.正常应用（{"label.apptype" :
     * "general"}） 4.未知应用（{"label.apptype" : "unknown"}） 5.其它应用（{"label.apptype" : "other"}）
     */
    private String applicationType;
    /* 会话加密标识(null:未知;false:非加密;true:加密) */
    private Boolean enc;
    /* 资产标识(0:无资产;1:client为资产;2:server为资产;3:双方均为资产) */
    private Integer assetFlg;
    /* 拓展标识 */
//    private Map<String, Object> extLabel = new HashMap<>();


    @Override
    public void adjust() {
        this.applicationType = "unknown";
    }

    @Override
    public void merge(Object t) {
        AbstractDataWarehouseData abstractDataWarehouseData = (AbstractDataWarehouseData) t;
    }

    @Override
    public String getId() {
        return this.id;
    }
}