package com.tincery.gaea.core.base.component.support;

import com.tincery.gaea.core.dw.MergeAble;
import com.tincery.starter.base.dao.SimpleBaseDaoImpl;
import com.tincery.starter.base.model.SimpleBaseDO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MergeSupport {

    /****
     * 复查  如果数据库中有相同ID的信息 整合更新
     * @param dao 相应的dao层实体
     * @param list  已经计算好的数据
     **/
    public static <T extends SimpleBaseDO & MergeAble<T>> void rechecking(SimpleBaseDaoImpl<T> dao, List<T> list) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(list.stream().map(data -> data.getId()).collect(Collectors.toList())));
        List<T> mongoData = dao.findListData(query);
        if (!CollectionUtils.isEmpty(mongoData)) {
            list.forEach(asset -> {
                for (T mergeData : mongoData) {
                    if (mergeData.getId().equals(asset.getId())) {
                        asset.merge(mergeData);
                        break;
                    }
                }
            });
        }
    }
}
