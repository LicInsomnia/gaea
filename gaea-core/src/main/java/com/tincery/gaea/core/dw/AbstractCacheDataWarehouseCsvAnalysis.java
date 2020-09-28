package com.tincery.gaea.core.dw;

import com.tincery.gaea.core.base.plugin.csv.CsvRow;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 需要缓存合并的实体
 **/
public abstract class AbstractCacheDataWarehouseCsvAnalysis implements DataWarehouseLineMultiAnalysis {


    final Map<Class<? extends MergeAble>,Integer> indexMap;

    final Map<String,MergeAble>[] cacheMap;

    protected AbstractCacheDataWarehouseCsvAnalysis(@NotNull Class<MergeAble>... clazz) {
        indexMap = new HashMap<>(clazz.length/3*4+1);
        cacheMap = new HashMap[clazz.length];
        for (int i = 0; i < clazz.length; i++) {
            indexMap.put(clazz[i],i);
            cacheMap[i] = new HashMap<>();
        }
    }


    public void append(CsvRow csvRow){
        MergeAble[] packs = this.pack(csvRow);
        for (MergeAble pack : packs) {
            Map<String, MergeAble> cache = cacheMap[indexMap.get(pack.getClass())];
            if(cache.containsKey(pack.getId())){
                MergeAble mergeAble = cache.get(pack.getId());
                mergeAble.merge(pack);
            }else{
                cache.put(pack.getId(),pack);
            }
        }
    }
}
