package com.tincery.gaea.api.dm.assetextension;

import com.tincery.gaea.core.dw.MergeAble;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class BaseAssetExtension implements MergeAble<BaseAssetExtension> {

    @Id
    protected String id;
    protected Long count;
    protected Long pkt;
    protected Long byteNum;

    @Override
    public BaseAssetExtension merge(BaseAssetExtension that) {
        this.count += that.getCount();
        this.pkt += that.getPkt();
        this.byteNum += that.getByteNum();
        return this;
    }

    public abstract void setKey();

}
