package com.tincery.gaea.api.dw;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.api.base.DnsRequestBO;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.src.extension.*;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AbstractDataWarehouseData extends AbstractMetaData implements MergeAble {

    protected String id;
    /**
     * 会话双方十进制IP地址
     */
    protected Long clientIpN;
    protected Long serverIpN;
    /**
     * 会话双方地理位置信息
     */
    protected Location clientLocation;
    protected Location serverLocation;
    /**
     * 拓展信息根据会话协议有差异
     */
    protected String tag;
    protected String keyWord;
    /**
     * 会话标签，标记协议名或malformed
     */
    protected String extensionFlag;
    /**
     * 各协议不同的拓展信息
     */
    protected SessionExtension sessionExtension;
    protected SslExtension sslExtension;
    protected OpenVpnExtension openVpnExtension;
    protected DnsExtension dnsExtension;
    protected SshExtension sshExtension;
    protected HttpExtension httpExtension;
    protected IsakmpExtension isakmpExtension;
    protected FtpAndTelnetExtension ftpAndTelnetExtension;
    protected EspAndAhExtension espAndAhExtension;
    protected MalformedExtension malformedExtension;
    /**
     * 键值参考sys.common_config.reorganization.value.cerkeys
     */
    protected JSONObject cer;
    protected DnsRequestBO dnsRequestBO;
    /**
     * 标签信息根据属性抽象
     */
    protected String dataSource;
    protected Integer dataType;
    protected Boolean protocolKnown;
    protected Boolean appKnown;
    protected Boolean malFormed;
    protected Boolean foreign;
    /**
     * 会话加密标识(null:未知;false:非加密;true:加密)
     */
    protected Boolean enc;
    /**
     * 资产标识(0:无资产;1:client为资产;2:server为资产;3:双方均为资产)
     */
    protected Integer assetFlag;

    /**
     * 拓展标识
     */
    @Override
    public void adjust() {
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
