package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.src.extension.BitCoinExtension;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BitCoinData extends AbstractSrcData {

    private List<BitCoinExtension> bitCoinExtension;

    private Integer version;

    private Integer size;


    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{
                super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                this.version,this.size,
                bitCoinExtensionToJsonString(this.bitCoinExtension),
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }


    private List<String> bitCoinExtensionToJsonString(List<BitCoinExtension> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        list.forEach(extension -> result.add(JSONObject.toJSON(extension).toString()));
        return result;
    }
}
