package com.tincery.gaea.datawarehouse.cer.execute;

import com.mongodb.client.model.BulkWriteOptions;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import org.bson.Document;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DatabaseUpdateModule extends BaseModule implements BaseModuleInterface {
    private Integer writeMongoLimit = 1000;

    public DatabaseUpdateModule() {
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
            System.out.println("DatabaseUpdateModule starts.");
            BulkWriteOptions options = new BulkWriteOptions();
            options.ordered(false);
            DataQueue queueInput = queuesInput.get(0);
            while (true) {
                CerData cer = (CerData)queueInput.poll(1, TimeUnit.SECONDS);
                if(cer != null) {
                    Document doc = new Document();
                    doc.put("selfsigned", cer.getSelfsigned());
                    doc.append("compliance", cer.getCompliance());
                    doc.append("compliancetype", cer.getCompliancetype());
                    doc.append("reliability", cer.getReliability());
                    doc.append("reliabilitytype", cer.getReliabilitytype());
                    doc.append("casetags", cer.getCasetags());
                    doc.append("compliancetags", cer.getCompliancetags());
                    doc.append("reliabilitytags", cer.getReliabilitytags());
                    doc.append("compliancedetail", cer.getCompliancedetail());
                    doc.append("reliabilitydetail", cer.getReliabilitydetail());
                    doc.append("gmcompliancetype", cer.getGmcompliancetype());
                    doc.append("gmcompliancedetail", cer.getGmcompliancedetail());
                    doc.append("gmcompliancetags", cer.getGmcompliancetags());
                    doc.append("altnamenum", cer.getAltnamenum());
                    doc.append("altnamewhitenum", cer.getAltnamewhitenum());
                    doc.append("altnamedganum", cer.getAltnamedganum());
                    doc.append("malicious_website", cer.getMalicious_website());
                    Config.certDao.updateData(cer.getId(), doc);
                }
                if (queueInput.isEnd()) {
                    break;
                }
            }
//            getReliabilityStatis();
//            getComplianceStatis();
            System.out.println("DatabaseUpdateModule ends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private void getReliabilityStatis() {
//        BasicDBObject newReliabilityObj = new BasicDBObject();
//        BulkWriteOptions options = new BulkWriteOptions();
//        options.ordered(false);
//        List<String> reliabilityTagType = Arrays.asList("可信", "不可信", "待证实");
//        MongoCollection<Document> table = config.getProductionMongoCollection(MongoCollectionIDs.SRC_CER_COLLECTION_ID);
//        List<BasicDBObject> bsonList = new ArrayList<>();
//        for(String type : reliabilityTagType) {
//            BasicDBObject searchQuery = new BasicDBObject();
//            searchQuery.put("reliabilitytags", type);
//            long count = MongoUtils.count(table, searchQuery);
//            BasicDBObject bson = new BasicDBObject();
//            bson.put("name", type);
//            bson.put("count", count);
//            bsonList.add(bson);
//        }
//        newReliabilityObj.put("details", bsonList);
//        newReliabilityObj.put("type", "reliability");
//        newReliabilityObj.put("_id", 1);
//        MongoCollection<Document> statisTable = config.getProductionMongoCollection(MongoCollectionIDs.SRC_CER_STATIS_COLLECTION_ID);
//        BasicDBObject findObj = new BasicDBObject();
//        findObj.put("type", "reliability");
//        findObj.put("_id", 1);
//        ArrayList<UpdateOneModel<Document>> updateList = new ArrayList<>();
//        updateList.add(new UpdateOneModel<>(findObj, new BasicDBObject("$set", newReliabilityObj), new UpdateOptions().upsert(true)));
//        BulkWriteResult result = statisTable.bulkWrite(updateList, options);
//        if(result.getModifiedCount() > 0 ) {
//            System.out.println("Reliability statis have been updated!");
//        }
//    }
//
//    private void getComplianceStatis() {
//        BasicDBObject newComplianceObj = new BasicDBObject();
//        BulkWriteOptions options = new BulkWriteOptions();
//        options.ordered(false);
//        List<String> complianceTagType = descriptionList;
//        MongoCollection<Document> table = config.getProductionMongoCollection(MongoCollectionIDs.SRC_CER_COLLECTION_ID);
//        List<BasicDBObject> bsonList = new ArrayList<>();
//        for(String type : complianceTagType) {
//            BasicDBObject searchQuery = new BasicDBObject();
//            searchQuery.put("compliancetags", type);
//            long count = MongoUtils.count(table, searchQuery);
//            BasicDBObject bson = new BasicDBObject();
//            bson.put("name", type);
//            bson.put("count", count);
//            bsonList.add(bson);
//        }
//        newComplianceObj.put("details", bsonList);
//        newComplianceObj.put("type", "compliance");
//        newComplianceObj.put("_id", 2);
//        MongoCollection<Document> statisTable = config.getProductionMongoCollection(MongoCollectionIDs.SRC_CER_STATIS_COLLECTION_ID);
//        BasicDBObject findObj = new BasicDBObject();
//        findObj.put("type", "compliance");
//        findObj.put("_id", 2);
//        ArrayList<UpdateOneModel<Document>> updateList = new ArrayList<>();
//        updateList.add(new UpdateOneModel<>(findObj, new BasicDBObject("$set", newComplianceObj), new UpdateOptions().upsert(true)));
//        BulkWriteResult result = statisTable.bulkWrite(updateList, options);
//        if(result.getModifiedCount() > 0 ) {
//            System.out.println("Compliance statis have been updated!");
//        }
//    }

}
