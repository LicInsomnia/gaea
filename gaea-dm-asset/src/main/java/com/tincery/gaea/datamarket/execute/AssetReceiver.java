package com.tincery.gaea.datamarket.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.AssetCondition;
import com.tincery.gaea.api.src.SessionData;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.config.ApplicationInfo;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.util.List;

/**
 * @author gxz
 */
@Slf4j
@Setter
@Getter
@Service
public class AssetReceiver implements Receiver {

    List<AssetCondition> whiteListConditions;
    List<AssetCondition> blackListConditions;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject assetJson = JSON.parseObject(line);
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


    @Override
    public void init() {

    }
}
