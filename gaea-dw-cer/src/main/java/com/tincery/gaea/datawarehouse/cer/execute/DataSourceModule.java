package com.tincery.gaea.datawarehouse.cer.execute;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.tincery.gaea.api.base.CertDo;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import org.bson.Document;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuming
 */
public class DataSourceModule extends BaseModule implements BaseModuleInterface {

    private int readMongoLimit = 10000;

    public DataSourceModule() {
        super();
    }

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 1);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 4);
    }

    @Override
    public void run() {
        System.out.println("DataSourceModule starts.");
        DataQueue queueInput = queuesInput.get(0);
        Set<String> shaSet = new HashSet<>();
        while (true) {
            CerData cer = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
            if(cer != null) {
                try {
                    shaSet.add(cer.getId());
                    for (DataQueue queue : queuesOutput) {
                        queue.put(cer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (queueInput.isEnd()) {
                break;
            }
        }
        List<CertDo> dataList = Config.certDao.getDataList(readMongoLimit);
        for(CertDo certDo : dataList) {
            CerData data = new CerData();
            BeanUtils.copyProperties(certDo, data);
            if(shaSet.contains(data.getId())) {
                continue;
            }
            for (DataQueue queue : queuesOutput) {
                queue.put(data);
            }
        }
        for (DataQueue queue : queuesOutput) {
            queue.detach();
        }
        System.out.println("DataSourceModule ends.");
    }

    private void createIndex(MongoCollection<Document> table, String field) {
        //检测并新建索引
        boolean indexFlag = false;
        ListIndexesIterable<Document> indexList = table.listIndexes();
        for (Document document : indexList) {
            if (document.get("name").toString().equals(field + "_-1")) {
                indexFlag = true;
            }
        }
        if (!indexFlag) {
            table.createIndex(new Document("captime_n", -1));
            System.out.println("Create captime_n index.");
        }
    }
}