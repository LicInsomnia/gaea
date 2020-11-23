package com.tincery.gaea.api.dm.assetextension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
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

    public abstract boolean create(JSONObject jsonObject);

    protected void appendFlow(JSONObject jsonObject) {
        this.count = 1;
        this.pkt = jsonObject.getLong(HeadConst.FIELD.UP_PKT) + jsonObject.getLong(HeadConst.FIELD.DOWN_PKT);
        this.byteNum = jsonObject.getLong(HeadConst.FIELD.UP_BYTE) + jsonObject.getLong(HeadConst.FIELD.DOWN_BYTE);
    }

    public abstract void setKey();

}
