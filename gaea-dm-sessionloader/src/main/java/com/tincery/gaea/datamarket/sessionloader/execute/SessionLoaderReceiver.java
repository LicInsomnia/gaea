package com.tincery.gaea.datamarket.sessionloader.execute;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Service
public class SessionLoaderReceiver implements Receiver {

    @Override
    public void init() {

    }

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        String impSessionFileName = textMessage.getText();
        File impSessionFile = new File(impSessionFileName);
        if (!impSessionFile.exists()) {
            return;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(impSessionFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    AbstractDataWarehouseData data = JSONObject.parseObject(line, AbstractDataWarehouseData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(1);
    }

    private void free() {

    }

}
