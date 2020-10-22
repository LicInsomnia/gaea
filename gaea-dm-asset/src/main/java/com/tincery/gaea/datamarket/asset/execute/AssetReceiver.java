package com.tincery.gaea.datamarket.asset.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.AssetConfigDO;
import com.tincery.gaea.api.dm.AssetDataDTO;
import com.tincery.gaea.api.dm.AssetCondition;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.dao.AssetConditionDao;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class AssetReceiver implements Receiver {

    @Autowired
    private AssetConditionDao assetConditionDao;

    List<AssetCondition> whiteListConditions;
    List<AssetCondition> blackListConditions;


    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
        List<JSONObject> allJsonObject = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject assetJson = JSON.parseObject(line);
                allJsonObject.add(assetJson);
                // 如果黑名单命中
                if (blackListConditions.stream().anyMatch(condition -> condition.hit(assetJson))) {
                    System.out.println("告警");
                    continue;
                }

                if (whiteListConditions.stream().noneMatch(condition -> condition.hit(assetJson))) {
                    System.out.println("告警");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /****
     *
     * @author gxz
     * @param  jsonObject
     **/
    private List<AssetConfigDO> detector(JSONObject jsonObject){

    }


    private List<AssetDataDTO> assetIpGroup(List<JSONObject> assetJsons){
        List<AssetDataDTO> insertLists = new ArrayList<>();
        Map<String, List<JSONObject>> collect = assetJsons.stream().collect(Collectors.groupingBy(json->json.getString("ip")));
        collect.forEach((groupField,list)->{

        });
    }

    @Override
    public void init() {
        whiteListConditions = new ArrayList<>();
        blackListConditions = new ArrayList<>();
        List<AssetCondition> activityData = assetConditionDao.findActivityData();
        activityData.forEach((condition) -> {
            if (condition.isBlackList()) {
                blackListConditions.add(condition);
            } else {
                whiteListConditions.add(condition);
            }
        });

    }
}
