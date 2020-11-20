package com.tincery.gaea.api.dm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class AssetCondition extends SimpleBaseDO {


    @Id
    private String id;

    private String proname;

    private String description;

    private List<ConditionGroup> conditionGroup;

    @Setter
    @Getter
    public static class ConditionGroup {
        List<FieldCondition> conditions;
        List<CerLink> certLinks;
        String description;

        public boolean hit(JSONObject assetJson,AssetCondition cerCondition) {
            if (!conditions.stream().allMatch(fieldCondition -> fieldCondition.hit(assetJson))) {
                return false;
            }
            if (CollectionUtils.isEmpty(certLinks)) {
                return true;
            }
            JSONArray certChain = assetJson.getJSONArray(HeadConst.FIELD.SERVER_CER_CHAIN);
            return certLinks.stream().allMatch(cerLink -> cerLink.certHit(certChain,cerCondition));
        }

    }

    @Setter
    @Getter
    public static class CerLink {
        private int cerIndex;
        private List<Integer> cerConditionIndex;

        public boolean certHit(JSONArray certChain,AssetCondition cerCondition) {
            if (certChain == null || certChain.size() < cerIndex) {
                return false;
            }
            JSONObject certJson = certChain.getJSONObject(cerIndex);
            List<ConditionGroup> certCondition = cerCondition.getConditionGroup();
            return cerConditionIndex.stream().map(certCondition::get)
                    .anyMatch(certConditionGroup -> certConditionGroup.hit(certJson,cerCondition));
        }
    }


}
