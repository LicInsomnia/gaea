package com.tincery.gaea.datawarehouse.cer.execute;

import com.mongodb.client.model.BulkWriteOptions;
import com.tincery.gaea.api.src.CerData;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModule;
import com.tincery.gaea.core.base.tool.moduleframe.BaseModuleInterface;
import com.tincery.gaea.core.base.tool.moduleframe.DataQueue;
import org.bson.Document;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liuming
 */
public class DatabaseUpdateModule extends BaseModule implements BaseModuleInterface {
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
                CerData cer = (CerData) queueInput.poll(1, TimeUnit.SECONDS);
                if (cer != null) {
                    Document doc = new Document();
                    doc.put("selfSigned", cer.getSelfSigned());
                    doc.append("compliance", cer.getCompliance());
                    doc.append("complianceType", cer.getComplianceType());
                    doc.append("reliability", cer.getReliability());
                    doc.append("reliabilityType", cer.getReliabilityType());
                    doc.append("caseTags", cer.getCaseTags());
                    doc.append("complianceTags", cer.getComplianceTags());
                    doc.append("reliabilityTags", cer.getReliabilityTags());
                    doc.append("complianceDetail", cer.getComplianceDetail());
                    doc.append("reliabilityDetail", cer.getReliabilityDetail());
                    doc.append("gmcomplianceType", cer.getGmcomplianceType());
                    doc.append("gmcomplianceDetail", cer.getGmcomplianceDetail());
                    doc.append("gmcomplianceTags", cer.getGmcomplianceTags());
                    doc.append("altNameNum", cer.getAltNameNum());
                    doc.append("altNameWhiteNum", cer.getAltNameWhiteNum());
                    doc.append("altNameDgaNum", cer.getAltNameDgaNum());
                    doc.append("maliciousWebsite", cer.getMaliciousWebsite());
                    Config.certDao.updateData(cer.getId(), doc);
                }
                if (queueInput.isEnd()) {
                    break;
                }
            }
            System.out.println("DatabaseUpdateModule ends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
