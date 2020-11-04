package com.tincery.gaea.ods.httpanalysis.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.MatchHttpConfig;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.ods.httpanalysis.MatchHttpApplication;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@SpringBootTest(classes = MatchHttpApplication.class)
class HttpAnalysisReceiverTest {

    @Autowired
    private Receiver receiver;
    @Resource(name = "sysMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Test
    public void aa() throws JMSException {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("/Users/gongxuanzhang/Downloads/http_1604468929377.json");
        receiver.receive(activeMQTextMessage);
    }

    @Test
    public void bb() {
        List<JSONObject> all = mongoTemplate.findAll(JSONObject.class, "match_http_analysis_config");
        List<MatchHttpConfig> collect = all.stream().map(this::map).collect(Collectors.toList());
        mongoTemplate.insert(collect,"match_http_config");
    }

    public MatchHttpConfig map(JSONObject jsonObject) {
        MatchHttpConfig matchHttpConfig = new MatchHttpConfig();
        matchHttpConfig.setId(jsonObject.getString("_id"));
        JSONArray regularRule = jsonObject.getJSONArray("regularRule");
        Map<String, List<Map>> matchMap =
                regularRule.stream().map(x -> (Map) x).collect(Collectors.groupingBy(x -> x.get("matchStr").toString()));
        List<MatchHttpConfig.Extract> extracts = new ArrayList<>();
        matchMap.forEach((matchStr, list) -> {
            MatchHttpConfig.Extract result = new MatchHttpConfig.Extract();
            result.setMatchStr(matchStr);
            List<List<JSONObject>> extractFeatures =
                    list.stream().map(jsonObject1 -> new JSONObject(jsonObject1).getJSONArray(
                    "extractFeatures")).map(x -> x.toJavaList(JSONObject.class)).collect(Collectors.toList());
            List<List<MatchHttpConfig.Match>> matchs = new ArrayList<>();
            for (List<JSONObject> extractFeature : extractFeatures) {
                matchs.add(extractFeature.stream().map(jsonObject1 -> {
                    MatchHttpConfig.Match match = new MatchHttpConfig.Match();
                    Set<String> strings = jsonObject1.keySet();
                    if (strings.equals("option")) {
                        match.setRequire(false);
                    } else {
                        match.setRequire(true);
                    }
                    if (strings.equals("decode")) {
                        match.setDecode(jsonObject1.getIntValue("decode"));
                    }
                    jsonObject1.forEach((key, value) -> {
                        if (!key.equals("option") && !key.equals("decode")) {
                            match.setName(key);
                            int i = value.toString().indexOf("(?)");
                            System.out.println(value);
                            match.setSubStart(value.toString().substring(0,i));
                            match.setSubEnd(value.toString().substring(i+3));
                        }
                    });
                    // json变成Match
                    return match;
                }).collect(Collectors.toList()));
            }
            result.setItems(matchs);
            extracts.add(result);
        });
        return matchHttpConfig.setExtract(extracts);
    }

}
