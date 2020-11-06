package com.tincery.gaea.api.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gongxuanzhang
 */
@ToString
@Setter
@Getter
public class MatchHttpConfig extends SimpleBaseDO {

    @Id
    private String id;
    private List<Extract> extract;


    public static JSONObject groupHitAndFillInfo(List<MatchHttpConfig.Match> matches,JSONObject httpJson){
        JSONObject clone = (JSONObject)httpJson.clone();
        for (Match match : matches) {
            if(!match.hit(clone)){
                return null;
            }
        }
        return clone;
    }

    @ToString
    @Setter
    @Getter
    public static class Extract {
        private String description;
        private String matchStr;
        private boolean trash;
        private List<List<Match>> items;
    }

    @ToString
    @Setter
    @Getter
    public static class Match {
        private int order;
        private int subCount;
        private String subStart;
        private String subEnd;
        private String name;
        private int decode;
        private boolean require;

        public boolean hit(JSONObject jsonObject){
            String content = jsonObject.getString("content");
            int startIndex = content.indexOf(subStart);
            if(startIndex==-1){
                return require;
            }
            int endIndex = content.indexOf(subEnd,startIndex);
            if(endIndex==-1){
                return require;
            }
            String value = content.substring(startIndex, endIndex);
            // 如果value值是"" 直接返回 不打印在info上面
            if(value.isEmpty()){
                return true;
            }
            JSONArray info = jsonObject.getJSONArray("information");
            if(CollectionUtils.isEmpty(info)){
                info = new JSONArray();
            }
            info.add(new NameAndValue(name,value));
            jsonObject.put("info",info);
            return true;
        }
    }

}
