package com.tincery.dw.commomappdetect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tincery.dw.commomappdetect.execute.CommonSearchReceiver;
import com.tincery.gaea.api.base.AppDetect;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import javax.jms.MessageNotWriteableException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest(classes = CommonAppdetectApplication.class)
class CommonAppdetectApplicationTest {

    @Autowired
    private CommonSearchReceiver commonSearchReceiver;



    @Resource(name="sysMongoTemplate")
    private MongoTemplate mongoTemplate;


    @Test
    public void aa() throws MessageNotWriteableException {
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("asdf");
        commonSearchReceiver.receive(activeMQTextMessage);
    }

    @Test
    public void find(){
        Map<String,String> ca = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String s = ca.computeIfAbsent("asdf", (k) -> {
                System.out.println("asdf");
                return "asdf";
            });
        }
    }

    @Test
    public void insert(){
       /* AppDetect appDetect = new AppDetect();
        AppDetect.AlertInfo info = new AppDetect.AlertInfo();
        info.setCategory("dynamic").setSubcategoryDesc("SouluoVpn")
                .setSubcategory("special_app").setCategoryDesc("VPN翻墙").setLevel(3).setAccuracy("疑似").setRemark("准确率90%，三步，ssl/dns/ssl证书");
        appDetect.setId("Vpn360").setAlertInfo(info);
        ApplicationInformationBO applicationInformationBO = new ApplicationInformationBO();
        applicationInformationBO.setType(Arrays.asList("重点关注@重点关注","日常生活@VPN翻墙")).setSpecialTag(Arrays.asList("VPN翻墙"))
                .setTitle("TorVpn_forwin");
        appDetect.setAppInfo(applicationInformationBO);
        List<SearchCondition> list = new ArrayList<>();
        list.add((SearchCondition)new SearchCondition().setOrder(1).setField("servername").setValue("crashlytics.com").setType(STRING).setOperator(EQUALS));
        list.add((SearchCondition)new SearchCondition().setOrder(2).setField("domain").setValue("www.vpngate.net").setType(STRING).setOperator(NO_EXIST));
        list.add((SearchCondition)new SearchCondition().setOrder(3).setField("clientCerchain").setValue("#mongo#x509cert#_id").setType(STRING).setOperator(IN));
        list.add((SearchCondition)new SearchCondition().setOrder(4).setField("subject_cn").setValue("www.softether.com").setType(STRING).setOperator(EQUALS));
        appDetect.setConditions(list);
        List<SearchRule> ruleList = new ArrayList<>();
        KV<String,List<String>> kv1 = new KV<>("ssl",Arrays.asList("1"));
        KV<String,List<String>> kv2 = new KV<>("ssl",Arrays.asList("2"));
        KV<String,List<String>> kv3 = new KV<>("ssl",Arrays.asList("3,-4"));
        ruleList.add(new SearchRule().setMatch(kv1).setCount(1));
        ruleList.add(new SearchRule().setMatch(kv2).setCount(1));
        ruleList.add(new SearchRule().setMatch(kv3).setCount(1));
        appDetect.setRules(ruleList);
        appDetect.setDescription("SouluoVpn相关配置");
        appDetect.setDuration(300L);
        mongoTemplate.insert(appDetect,"app_detect");*/
       Query query = new Query().skip(2);
        List<JSONObject> appdetect_config = mongoTemplate.find(query,JSONObject.class, "appdetect_config");
        appdetect_config.stream().map(this::bb).forEach(mongoTemplate::insert);
    }

    public AppDetect bb(JSONObject jsonObject){



        AppDetect appDetect = new AppDetect();
        appDetect.setId(jsonObject.getString("_id"));
        appDetect.setDescription(jsonObject.getString("description"));
        JSONObject value = jsonObject.getJSONObject("value");
        ApplicationInformationBO appInfo = value.getJSONObject("appinfo").toJavaObject(ApplicationInformationBO.class);
        AppDetect.AlertInfo alertInfo = value.getJSONObject("alertinfo").toJavaObject(AppDetect.AlertInfo.class);
        appDetect.setAppInfo(appInfo).setAlertInfo(alertInfo).setDuration(value.getLong("validtime"));
        JSONArray rule = value.getJSONArray("rule");
        return appDetect;

    }

}
