package com.tincery.gaea.datawarehouse.cer.execute;

import com.tincery.gaea.core.base.component.Receiver;
import com.tincery.gaea.core.base.component.support.ApplicationCheck;
import com.tincery.gaea.core.base.component.support.AssetDetector;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.component.support.WebCheck;
import com.tincery.gaea.core.base.dao.CertDao;
import com.tincery.gaea.core.base.dao.SrcRuleDao;
import com.tincery.gaea.core.base.mgt.AlarmDictionary;
import com.tincery.gaea.core.base.tool.moduleframe.ModuleConnection;
import com.tincery.gaea.core.base.tool.moduleframe.ModuleManager;
import com.tincery.gaea.core.base.tool.moduleframe.ModuleTopology;
import com.tincery.gaea.datawarehouse.cer.config.property.CerProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

import static com.tincery.gaea.core.base.mgt.HeadConst.GORGEOUS_DIVIDING_LINE;

@Slf4j
@Setter
@Getter
@Service
public class CerReceiver implements Receiver {
    private List<Document> defaultAlarmList;
    private Document gmConfigMap;
    private Document defaultConfig;
    @Autowired
    private CertDao certDao;

    @Autowired
    private CerProperties cerProperties;

    @Autowired
    private SrcRuleDao srcRuleDao;

    @Autowired
    AlarmDictionary alarmDictionary;

    @Autowired
    protected IpSelector ipSelector;

    /**
     * 资产检测模块
     */
    @Autowired
    protected AssetDetector assetDetector;

    @Autowired
    ApplicationCheck appCheck;

    @Autowired
    WebCheck webCheck;

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        System.out.println(textMessage.getText());
        run(null);
        System.out.println("spring 结束了");
    }

    @Override
    public void init() {
        System.out.println("spring加载完了");
//        train();
        Config.init(certDao, cerProperties, srcRuleDao, alarmDictionary, ipSelector, assetDetector, appCheck, webCheck);
        defaultAlarmList = cerProperties.getDefaultAlarm();
        gmConfigMap = cerProperties.getGmConfig();
        defaultConfig = cerProperties.getDefaultConfig();
    }

    public void run(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("X509cert Analysis starting...");
        System.out.println(GORGEOUS_DIVIDING_LINE);
        Map<String, ModuleConnection> topology = getTopolgy();
        ModuleManager manager = new ModuleManager(topology, 15, "com.tincery.gaea.datawarehouse.cer.execute", args);
        if (!manager.init()) {
            System.err.println("Manager init error.");
            return;
        }
        manager.run(false);
        System.out.println("Analysis finished in " + (System.currentTimeMillis() - startTime) + "ms...");
    }

    private Map<String, ModuleConnection> getTopolgy() {
        ModuleTopology topology = new ModuleTopology();
        topology.addOutputTag("DataSourceCheckSigModule", "DataSourceCheckSigModule->CerCheckSigModule");
        topology.addInputTag("CerCheckSigModule", "DataSourceCheckSigModule->CerCheckSigModule");

        topology.addOutputTag("CerCheckSigModule", "CerCheckSigModule->DatabaseUpdateModule");
        topology.addInputTag("DatabaseUpdateModule", "CerCheckSigModule->DatabaseUpdateModule");

        topology.addOutputTag("DataSourceModule", "DataSourceModule->CerComplianceModule");
        topology.addOutputTag("DataSourceModule", "DataSourceModule->CerReliabilityModule");
        topology.addOutputTag("DataSourceModule", "DataSourceModule->CerGmComplianceModule");

        topology.addInputTag("CerComplianceModule", "DataSourceModule->CerComplianceModule");
        topology.addInputTag("CerReliabilityModule", "DataSourceModule->CerReliabilityModule");
        topology.addInputTag("CerGmComplianceModule", "DataSourceModule->CerGmComplianceModule");

        topology.addOutputTag("CerComplianceModule", "CerComplianceModule->DataMergeModule");
        topology.addOutputTag("CerReliabilityModule", "CerReliabilityModule->DataMergeModule");
        topology.addOutputTag("CerGmComplianceModule", "CerGmComplianceModule->DataMergeModule");

        topology.addInputTag("DataMergeModule", "CerComplianceModule->DataMergeModule");
        topology.addInputTag("DataMergeModule", "CerReliabilityModule->DataMergeModule");
        topology.addInputTag("DataMergeModule", "CerGmComplianceModule->DataMergeModule");

        topology.addOutputTag("DataMergeModule", "DataMergeModule->AlertModule");
        topology.addOutputTag("DataMergeModule", "DataMergeModule->MarkModule");

        topology.addInputTag("AlertModule", "DataMergeModule->AlertModule");
        topology.addInputTag("MarkModule", "DataMergeModule->MarkModule");

        topology.addOutputTag("MarkModule", "MarkModule->DatabaseUpdateModule");

        topology.addInputTag("DatabaseUpdateModule", "MarkModule->DatabaseUpdateModule");
        topology.construct();
        return topology.getTopology();
    }

    public void train() {
        WebTrain train = new WebTrain();
        train.initiate();
        String acceptedChars = "abcdefghijklmnopqrstuvwxyz1234567890-";
        train.dgaTrain(acceptedChars,
                "D:\\domain\\porn.txt",
                "D:\\domain\\pornbad.txt",
                "D:\\domain\\porngood.txt",
                "porn");

        train.dgaTrain(acceptedChars,
                "D:\\domain\\pinyin.txt",
                "D:\\domain\\bad.txt",
                "D:\\domain\\pinyingood.txt",
                "pinyin");
        train.dgaTrain(acceptedChars,
                "D:\\domain\\alexatrain.txt",
                "D:\\domain\\alexabad.txt",
                "D:\\domain\\alexagood.txt",
                "alexa");

        train.dgaTrain(acceptedChars,
                "D:\\domain\\eng.txt",
                "D:\\domain\\bad.txt",
                "D:\\domain\\enggood.txt",
                "eng");
        train.classifyTrain("D:\\malicious\\new\\total161.arff", "rss");
    }

}
