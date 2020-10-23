package com.tincery.gaea.core.src;

import com.tincery.gaea.api.base.AbstractMetaData;
import com.tincery.gaea.core.base.component.LineAnalysis;

import java.util.Map;

public interface SrcByteArrayAnalysis <T extends AbstractMetaData> extends LineAnalysis<Map.Entry<String, byte[]>,T> {

}
