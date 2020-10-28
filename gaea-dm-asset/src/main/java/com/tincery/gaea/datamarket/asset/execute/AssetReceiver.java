package com.tincery.gaea.datamarket.asset.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.api.dm.AssetConfigDO;
import com.tincery.gaea.api.dm.AssetConfigs;
import com.tincery.gaea.api.dm.AssetDataDTO;
import com.tincery.gaea.api.dm.AssetCondition;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.dao.AssetConditionDao;
import jdk.nashorn.internal.ir.EmptyNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
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
    private AssetDetector assetDetector;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        File file = new File(textMessage.getText());
        List<JSONObject> allJsonObject = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject assetJson = JSON.parseObject(line);
                int assetFlag = assetJson.getIntValue("assetFlag");
                JSONObject jsonObject = AssetFlag.jsonRun(assetFlag, assetJson);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init() {

    }

    public enum AssetFlag {

        NOT_ASSET(0, (json,detector) -> null),
        CLIENT_ASSET(1, AssetConfigs::detectorClient),
        SERVER_ASSET(2,  AssetConfigs::detectorServer),
        SERVER_AND_CLIENT_ASSET(3,  AssetConfigs::detectorClientAndServer);

        private int flag;

        private BiFunction<JSONObject, AssetDetector, List<AlarmMaterialData>> function;

        AssetFlag(int flag, BiFunction<JSONObject, AssetDetector, List<AlarmMaterialData>> function) {
            this.flag = flag;
            this.function = function;
        }

        private static AssetFlag findByFlag(int flag) {
            Optional<AssetFlag> first = Arrays.stream(values()).filter(assetFlag -> assetFlag.flag == flag).findFirst();
            return first.orElse(null);
        }

        public static List<AlarmMaterialData> jsonRun(int flag, JSONObject assetJson, AssetDetector assetDetector) {
            return findByFlag(flag).function.apply(assetJson, assetDetector);
        }
    }

}
