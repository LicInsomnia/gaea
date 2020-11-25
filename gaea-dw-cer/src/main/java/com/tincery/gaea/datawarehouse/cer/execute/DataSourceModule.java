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

import java.util.List;

public class DataSourceModule extends BaseModule implements BaseModuleInterface {

    private int readMongoLimit = 10000;

    public DataSourceModule() {
        super();
    }

    @Override
    public boolean setInput(List<DataQueue> queues) {
        return super.setInput(queues, 0);
    }

    @Override
    public boolean setOutput(List<DataQueue> queues) {
        return super.setOutput(queues, 3);
    }

    @Override
    public void run() {
        System.out.println("DataSourceModule starts.");
        List<CertDo> dataList = Config.certDao.getDataList(readMongoLimit);
        for(CertDo certDo : dataList) {
            CerData data = new CerData();
            BeanUtils.copyProperties(certDo, data);
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