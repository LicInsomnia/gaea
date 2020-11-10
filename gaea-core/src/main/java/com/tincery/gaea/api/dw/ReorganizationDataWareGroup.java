package com.tincery.gaea.api.dw;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Setter
@Getter
public class ReorganizationDataWareGroup {
    private List<AbstractDataWarehouseData> impsessionDataList;
    private List<AbstractDataWarehouseData> assetDataList;

    public static ReorganizationDataWareGroup init(){
        ReorganizationDataWareGroup result = new ReorganizationDataWareGroup();
        result.impsessionDataList = new ArrayList<>();
        result.assetDataList = new ArrayList<>();
        return result;
    }

    public static ReorganizationDataWareGroup concurrentInit(){
        ReorganizationDataWareGroup result = new ReorganizationDataWareGroup();
        result.impsessionDataList = new CopyOnWriteArrayList<>();
        result.assetDataList = new CopyOnWriteArrayList<>();
        return result;
    }
}
