package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class BaseAssetExtension implements MergeAble<BaseAssetExtension> {

    @Id
    protected String id;
    protected long count;
    protected long pkt;
    protected long byteNum;

    @Override
    public BaseAssetExtension merge(BaseAssetExtension that) {
        this.count += that.getCount();
        this.pkt += that.getPkt();
        this.byteNum += that.getByteNum();
        return this;
    }

    public abstract void create(JSONObject jsonObject);

    public abstract void setKey();

}
