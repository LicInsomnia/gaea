package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.api.base.CerChainDO;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import org.bson.Document;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DatabaseUpdateCerChainModule extends BaseModule implements BaseModuleInterface {
    public DatabaseUpdateCerChainModule() {
        super();
    }

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 1);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 0);
    }

    @Override
    public void run() {
        try {
            System.out.println("DatabaseUpdateCerChainModule starts.");
            String[] cerKeys;
            if (Config.commonConfig.getCerKeys() != null) {
                cerKeys = Config.commonConfig.getCerKeys().toArray(new String[0]);
            } else {
                cerKeys = new String[0];
            }
            CerData cer = new CerData();
            Map<String, Method> methodMap = new HashMap<>();
            for(String key :cerKeys){
                Class<?> iClass = cer.getClass();
                String methodKey = key.substring(0,1).toUpperCase() + key.substring(1);
                Method getMethod = iClass.getMethod("get" + methodKey);
                methodMap.put(key, getMethod);
            }
            DataQueue queueInput = queuesInput.get(0);
            while (true) {
                cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
                if(cer != null) {
                    for(String cerChain : cer.getCerChain()) {
                        Document docSubCerchain = getDoc(cer, methodMap, cerChain);
                        CerChainDO docCerchain = new CerChainDO();
                        docCerchain.setId(cerChain);
                        docCerchain.setSubCerChain(new ArrayList<>(Arrays.asList(docSubCerchain)));
                        Config.cerChainDao.saveOrUpdate(docCerchain);
                    }
                }
                if (queueInput.isEnd()) {
                    break;
                }
            }
            System.out.println("DatabaseUpdateCerChainModule ends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document getDoc(CerData cer, Map<String, Method> methodMap, String cerChain) {
        Document docSubCerchain = new Document();
        try {
            docSubCerchain.put("cerChain", cerChain);
            docSubCerchain.put("strHashData", cer.getDigest());
            for(String key : methodMap.keySet()) {
                docSubCerchain.put(key, methodMap.get(key).invoke(cer));
            }
            if(cer.getCerChainWhite().contains(cerChain)) {
                docSubCerchain.put("strDecryptData", cer.getDigest());
                docSubCerchain.put("checkResult", 1);
            } else {
                docSubCerchain.put("checkResult", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docSubCerchain;
    }
}