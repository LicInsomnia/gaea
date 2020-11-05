package com.tincery.gaea.datamarket.alarmcombine.execute;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.base.AlarmMaterialData;
import com.tincery.gaea.core.base.component.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sparrow
 */
@Slf4j
@Service
public class AlarmCombineReceiver implements Receiver {

    private final List<AlarmMaterialData> alarmList = new CopyOnWriteArrayList<>();

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        String text = textMessage.getText();

    }

    @Override
    public void init() {

    }
}
