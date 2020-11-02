package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 一条资产信息通过层层过滤之后获得的报告 有告警报告和原始数据信息
 **/
@Setter
@Getter
public class AssetReport {

    List<JSONObject> assetJsons;

    List<AlarmMaterialData> alarmMaterialData;
}
