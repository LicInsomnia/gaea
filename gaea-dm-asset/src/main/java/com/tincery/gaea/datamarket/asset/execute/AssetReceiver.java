package com.tincery.gaea.datamarket.asset.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dm.AssetConfigs;
import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        NOT_ASSET(0, (json) -> json),
        CLIENT_ASSET(1, AssetConfigs::detectorClient),
        SERVER_ASSET(2, AssetConfigs::detectorServer),
        SERVER_AND_CLIENT_ASSET(3, AssetConfigs::detectorClientAndServer);

        private final int flag;

        private final Function<JSONObject, JSONObject> function;

        AssetFlag(int flag, Function<JSONObject, JSONObject> function) {
            this.flag = flag;
            this.function = function;
        }

        private static AssetFlag findByFlag(int flag) {
            Optional<AssetFlag> first = Arrays.stream(values()).filter(assetFlag -> assetFlag.flag == flag).findFirst();
            return first.orElse(null);
        }

        public static JSONObject jsonRun(int flag, JSONObject assetJson) {
            return findByFlag(flag).function.apply(assetJson);
        }

    }
}
